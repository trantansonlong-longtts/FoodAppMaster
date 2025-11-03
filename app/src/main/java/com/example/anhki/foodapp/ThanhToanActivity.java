package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher; // Import
import androidx.activity.result.contract.ActivityResultContracts; // Import
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Import
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Firebase Imports
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot; // Import QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.Exclude;

// Import DTO và Adapter mới
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiThanhToan; // Adapter RecyclerView
import com.example.anhki.foodapp.DTO.ChiTietGoiMonDTO; // DTO mới

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ThanhToanActivity extends AppCompatActivity implements View.OnClickListener, AdapterHienThiThanhToan.MonAnClickListener {
    private static final String TAG = "ThanhToanActivity";

    // Views
    private RecyclerView rvChiTietThanhToan; // Dùng RecyclerView
    private Button btnThanhToan, btnThoat;
    private TextView txtTongTien, txtTenBanThanhToan;

    // Data & Adapter
    private AdapterHienThiThanhToan adapter;
    private List<ChiTietGoiMonDTO> chiTietList;

    // Firebase
    private FirebaseFirestore db;
    private ListenerRegistration goiMonListener;    // Listener cho document goiMon (tổng tiền)
    private ListenerRegistration chiTietListener; // Listener cho sub-collection chiTietGoiMon
    private String banAnDocId;
    private String goiMonDocId; // ID của hóa đơn đang mở cần thanh toán
    private DocumentReference banRef; // Reference đến document bàn ăn

    // Launcher để nhận kết quả từ SoLuongActivity (chế độ sửa)
    private final ActivityResultLauncher<Intent> suaSoLuongLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Không cần làm gì, listener sẽ tự cập nhật tổng tiền
                    Toast.makeText(this, "Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
                }
            });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo layout này có RecyclerView với id rvChiTietThanhToan
        // và TextView với id txtTenBanThanhToan
        setContentView(R.layout.layout_thanhtoan);

        // Ánh xạ Views
        rvChiTietThanhToan = findViewById(R.id.rvChiTietThanhToan);
        btnThanhToan = findViewById(R.id.btnThanhToan);
        btnThoat = findViewById(R.id.btnThoatThanhToan);
        txtTongTien = findViewById(R.id.txtTongTien);
        txtTenBanThanhToan = findViewById(R.id.txtTenBanThanhToan);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Intent (Gửi từ HienThiBanAnFragment)
        banAnDocId = getIntent().getStringExtra("banAnDocId");
        String tenBan = getIntent().getStringExtra("tenBan");
        // Hiển thị tên bàn
        txtTenBanThanhToan.setText("Bàn: " + tenBan);

        // Kiểm tra ID bàn
        if (banAnDocId == null || banAnDocId.isEmpty()) {
            Log.e(TAG, "Lỗi: Không nhận được banAnDocId từ Intent.");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin bàn", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu thiếu ID bàn
            return;
        }
        banRef = db.collection("banAn").document(banAnDocId);

        // Cài đặt RecyclerView VÀ TRUYỀN LISTENER (this)
        chiTietList = new ArrayList<>();
        adapter = new AdapterHienThiThanhToan(this, chiTietList, this); // Truyền "this"
        rvChiTietThanhToan.setLayoutManager(new LinearLayoutManager(this));
        rvChiTietThanhToan.setAdapter(adapter);

        // Gán sự kiện click cho các button
        btnThanhToan.setOnClickListener(this);
        btnThoat.setOnClickListener(this);
    }

    // Bắt đầu lắng nghe dữ liệu khi Activity hiển thị
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart - Bắt đầu tìm và lắng nghe goiMon cho bàn: " + banAnDocId);
        findOpenGoiMonAndListen(); // Tìm hóa đơn đang mở và bắt đầu lắng nghe
    }

    // Dừng lắng nghe khi Activity không còn hiển thị
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop - Dừng lắng nghe goiMon và chiTiet.");
        // Gỡ bỏ các listener để tránh leak bộ nhớ và cập nhật không cần thiết
        if (goiMonListener != null) {
            goiMonListener.remove();
            goiMonListener = null;
        }
        if (chiTietListener != null) {
            chiTietListener.remove();
            chiTietListener = null;
        }
    }

    // Hàm tìm hóa đơn đang mở ("tinhTrang" == "false") của bàn hiện tại
    private void findOpenGoiMonAndListen() {
        if (banRef == null) return; // Thoát nếu không có banRef

        // Thực hiện truy vấn tìm kiếm
        db.collection("goiMon")
                .whereEqualTo("maBanRef", banRef)
                .whereEqualTo("tinhTrang", "false")
                .limit(1) // Chỉ cần tìm 1 hóa đơn đang mở
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            // Tìm thấy hóa đơn đang mở
                            DocumentSnapshot goiMonDoc = snapshot.getDocuments().get(0);
                            goiMonDocId = goiMonDoc.getId(); // Lưu lại ID hóa đơn
                            Log.d(TAG,"Tìm thấy goiMon đang mở: " + goiMonDocId);

                            // Bắt đầu lắng nghe thay đổi của hóa đơn này (tổng tiền)
                            listenToGoiMon(goiMonDoc.getReference());
                            // Bắt đầu lắng nghe thay đổi của chi tiết hóa đơn (danh sách món)
                            listenToChiTiet(goiMonDoc.getReference());
                            btnThanhToan.setEnabled(true); // Bật nút thanh toán
                        } else {
                            // Không tìm thấy hóa đơn nào đang mở cho bàn này
                            Log.w(TAG, "Không tìm thấy goiMon nào đang mở cho bàn: " + banAnDocId);
                            Toast.makeText(this, "Bàn này không có hóa đơn nào đang mở.", Toast.LENGTH_SHORT).show();
                            chiTietList.clear(); // Xóa danh sách (nếu có)
                            adapter.notifyDataSetChanged();
                            txtTongTien.setText(getString(R.string.tongcong) + " 0 VNĐ");
                            btnThanhToan.setEnabled(false); // Vô hiệu hóa nút thanh toán
                        }
                    } else {
                        // Lỗi khi truy vấn
                        Log.e(TAG, "Lỗi tìm kiếm goiMon: ", task.getException());
                        Toast.makeText(this, "Lỗi tải hóa đơn", Toast.LENGTH_SHORT).show();
                        btnThanhToan.setEnabled(false); // Vô hiệu hóa nút thanh toán
                    }
                });
    }

    // Lắng nghe thay đổi trên document 'goiMon' (chủ yếu để cập nhật tổng tiền)
    private void listenToGoiMon(DocumentReference goiMonRef) {
        // Hủy listener cũ nếu đang chạy
        if (goiMonListener != null) goiMonListener.remove();

        goiMonListener = goiMonRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Lỗi lắng nghe goiMon:", e);
                // Nếu lỗi là do không tìm thấy document (ví dụ đã thanh toán ở máy khác)
                if (e.getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                    Log.w(TAG, "GoiMon document không tồn tại (có thể đã bị thanh toán/xóa).");
                    Toast.makeText(this, "Hóa đơn này không còn tồn tại.", Toast.LENGTH_SHORT).show();
                    btnThanhToan.setEnabled(false);
                }
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Lấy tổng tiền từ document
                Long tongTienLong = snapshot.getLong("tongTien");
                long tongTienValue = (tongTienLong != null) ? tongTienLong : 0;

                // Định dạng và hiển thị tổng tiền
                txtTongTien.setText(getString(R.string.tongcong) + " " + formatVND(tongTienValue));
                Log.d(TAG,"Tổng tiền cập nhật: "+ formatVND(tongTienValue));

                // Kiểm tra lại trạng thái, nếu đã thành "true" thì vô hiệu hóa nút
                String tinhTrang = snapshot.getString("tinhTrang");
                if ("true".equals(tinhTrang)) {
                    Log.d(TAG, "GoiMon đã được thanh toán, vô hiệu hóa nút.");
                    btnThanhToan.setEnabled(false);
                }

            } else {
                // Document không tồn tại
                Log.d(TAG, "Document goiMon không tồn tại hoặc đã bị xóa");
                Toast.makeText(this, "Hóa đơn không còn tồn tại.", Toast.LENGTH_SHORT).show();
                btnThanhToan.setEnabled(false);
                chiTietList.clear(); // Xóa danh sách hiển thị
                adapter.notifyDataSetChanged();
                txtTongTien.setText(getString(R.string.tongcong) + " 0 VNĐ");
            }
        });
    }

    // Lắng nghe thay đổi trong sub-collection 'chiTietGoiMon' (danh sách món)
    private void listenToChiTiet(DocumentReference goiMonRef) {
        if (chiTietListener != null) chiTietListener.remove();
        chiTietListener = goiMonRef.collection("chiTietGoiMon")
                .orderBy("tenMonAn", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Lỗi lắng nghe chiTietGoiMon:", e); return;
                    }
                    if (snapshots != null) {
                        chiTietList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            try {
                                ChiTietGoiMonDTO chiTiet = doc.toObject(ChiTietGoiMonDTO.class);
                                chiTiet.setDocumentId(doc.getId()); // <-- LƯU LẠI ID CỦA DOCUMENT
                                chiTietList.add(chiTiet);
                            } catch (Exception convertError){
                                Log.e(TAG, "Lỗi convert chi tiết món: " + doc.getId(), convertError);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        Log.d(TAG,"Chi tiết hóa đơn cập nhật: " + chiTietList.size() + " món");
                    }
                });
    }

    // Xử lý sự kiện click button
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnThanhToan) {
            Log.d(TAG, "Nút Thanh Toán được nhấn.");
            thucHienThanhToan(); // Gọi hàm xử lý thanh toán
        } else if (id == R.id.btnThoatThanhToan) {
            Log.d(TAG, "Nút Thoát được nhấn.");
            finish(); // Đóng Activity
        }
    }

    // Hàm thực hiện logic thanh toán trên Firestore
    private void thucHienThanhToan() {
        // Kiểm tra lại xem có ID hóa đơn không
        if (goiMonDocId == null || goiMonDocId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy hóa đơn để thanh toán", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Thanh toán thất bại: goiMonDocId rỗng.");
            return;
        }

        DocumentReference goiMonRef = db.collection("goiMon").document(goiMonDocId);
        // Sử dụng WriteBatch để đảm bảo cả 2 cập nhật thành công hoặc thất bại cùng lúc
        WriteBatch batch = db.batch();
        // 1. Cập nhật trạng thái của goiMon thành "true" (đã thanh toán)
        batch.update(goiMonRef, "tinhTrang", "true");
        //Lưu thời điểm thanh toán
        batch.update(goiMonRef, "gioThanhToan", Timestamp.now());
        // 2. Cập nhật trạng thái của BanAn thành "false" (trống)
        batch.update(banRef, "tinhTrang", "false");
        // Thực thi batch write
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Thanh toán thành công cho goiMon: " + goiMonDocId);
                    Toast.makeText(this, getString(R.string.thanhtoanthanhcong), Toast.LENGTH_SHORT).show();
                    // Nút Thanh toán sẽ tự động bị vô hiệu hóa bởi goiMonListener
                    // Bạn có thể chọn đóng Activity luôn nếu muốn:
                    // finish();
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi thực thi batch
                    Log.w(TAG, "Lỗi khi thanh toán goiMon: " + goiMonDocId, e);
                    Toast.makeText(this, getString(R.string.loi) + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    // Hàm định dạng tiền tệ VND
    private String formatVND(long amount) {
        // Sử dụng NumberFormat cho Locale Vietnam để có định dạng chuẩn hơn
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        // Bỏ ký hiệu 'đ' nếu không muốn: ((DecimalFormat)currencyFormatter).applyPattern("#,###"); return currencyFormatter.format(amount) + " VNĐ";
        return currencyFormatter.format(amount); // Trả về dạng "123.456 ₫"
    }
    @Override
    public void onItemClick(int position) {
        if (chiTietList == null || position >= chiTietList.size()) return;
        ChiTietGoiMonDTO item = chiTietList.get(position);

        // Mở SoLuongActivity ở chế độ SỬA
        Intent intent = new Intent(this, SoLuongActivity.class);
        intent.putExtra("goiMonDocId", goiMonDocId); // ID của Hóa đơn
        intent.putExtra("chiTietDocId", item.getDocumentId()); // ID của Món trong Hóa đơn
        intent.putExtra("currentSoLuong", item.getSoLuong()); // Số lượng hiện tại
        intent.putExtra("giaTien", item.getGiaTien()); // Giá của món
        intent.putExtra("tenMonAn", item.getTenMonAn()); // Tên món

        suaSoLuongLauncher.launch(intent); // Dùng launcher để mở
    }

    @Override
    public void onItemLongClick(int position) {
        if (chiTietList == null || position >= chiTietList.size()) return;
        ChiTietGoiMonDTO item = chiTietList.get(position);

        // Hiển thị dialog xác nhận xóa
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa " + item.getSoLuong() + " " + item.getTenMonAn() + " khỏi hóa đơn?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    xoaMonKhoiHoaDon(item);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Hàm logic xóa món khỏi Firestore
    private void xoaMonKhoiHoaDon(ChiTietGoiMonDTO item) {
        if (goiMonDocId == null) return;

        DocumentReference goiMonRef = db.collection("goiMon").document(goiMonDocId);
        DocumentReference chiTietRef = goiMonRef.collection("chiTietGoiMon").document(item.getDocumentId());
        long tongTienMonXoa = item.getGiaTien() * item.getSoLuong();

        WriteBatch batch = db.batch();

        // 1. Xóa document món ăn trong chiTietGoiMon
        batch.delete(chiTietRef);

        // 2. Trừ tiền món đã xóa khỏi tổng tiền của goiMon cha
        batch.update(goiMonRef, "tongTien", FieldValue.increment(-tongTienMonXoa));

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa món thành công", Toast.LENGTH_SHORT).show();
                    // Listener sẽ tự động cập nhật lại danh sách và tổng tiền
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi xóa món", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Lỗi xóa món: " + item.getDocumentId(), e);
                });
    }
}