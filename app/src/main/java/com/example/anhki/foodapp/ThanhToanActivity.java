//package com.example.anhki.foodapp;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiThanhToan;
//import com.example.anhki.foodapp.DAO.BanAnDAO;
//import com.example.anhki.foodapp.DAO.GoiMonDAO;
//import com.example.anhki.foodapp.DTO.ThanhToanDTO;
//
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.util.List;
//import java.util.Locale;
//
//public class ThanhToanActivity extends AppCompatActivity implements View.OnClickListener {
//    private GridView gridView;
//    private Button btnThanhToan, btnThoat;
//    private TextView txtTongTien;
//    private GoiMonDAO goiMonDAO;
//    private BanAnDAO banAnDAO;
//    private List<ThanhToanDTO> thanhToanDTOList;
//    private AdapterHienThiThanhToan adapterHienThiThanhToan;
//
//    private int maban;
//    private int magoimon;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_thanhtoan);
//
//        // Ánh xạ View
//        gridView = findViewById(R.id.gvThanhToan);
//        btnThanhToan = findViewById(R.id.btnThanhToan);
//        btnThoat = findViewById(R.id.btnThoatThanhToan);
//        txtTongTien = findViewById(R.id.txtTongTien);
//
//        // Khởi tạo DAO
//        goiMonDAO = new GoiMonDAO(this);
//        banAnDAO = new BanAnDAO(this);
//
//        // Lấy mã bàn từ Intent
//        maban = getIntent().getIntExtra("maban", 0);
//
//        if (maban != 0) {
//            // Lấy mã gọi món dựa vào mã bàn
//            magoimon = (int) goiMonDAO.LayMaGoiMonTheoMaBan(maban, "false");
//            // Hiển thị danh sách món và tính tổng tiền
//            hienThiDanhSachVaTinhTongTien();
//        }
//
//        btnThanhToan.setOnClickListener(this);
//        btnThoat.setOnClickListener(this);
//    }
//
//    private void hienThiDanhSachVaTinhTongTien() {
//        thanhToanDTOList = goiMonDAO.LayDanhSachMonAnTheoMaGoiMon(magoimon);
//
//        adapterHienThiThanhToan = new AdapterHienThiThanhToan(this, R.layout.custom_layout_hienthithanhtoan, thanhToanDTOList);
//        gridView.setAdapter(adapterHienThiThanhToan);
//
//        // Tính tổng tiền
//        long tongTien = 0;
//        for (ThanhToanDTO thanhToanDTO : thanhToanDTOList) {
//            tongTien += (long) thanhToanDTO.getSoLuong() * thanhToanDTO.getGiatien();
//        }
//
//        // ĐỊNH DẠNG TỔNG TIỀN VÀ HIỂN THỊ
//        String tongTienFormatted = formatVND(tongTien);
//        txtTongTien.setText(getString(R.string.tongcong) + " " + tongTienFormatted);
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btnThanhToan) {
//            boolean kiemtrabanan = banAnDAO.CapNhatTinhTrangBan(maban, "false");
//            boolean kiemtragoimon = goiMonDAO.CapNhatTrangThaiGoiMonTheoMaBan(maban, "true");
//
//            if (kiemtrabanan && kiemtragoimon) {
//                Toast.makeText(this, getString(R.string.thanhtoanthanhcong), Toast.LENGTH_SHORT).show();
//                hienThiDanhSachVaTinhTongTien(); // Tải lại danh sách (sẽ trống) và reset tổng tiền về 0
//            } else {
//                Toast.makeText(this, getString(R.string.loi), Toast.LENGTH_SHORT).show();
//            }
//        } else if (id == R.id.btnThoatThanhToan) {
//            finish();
//        }
//    }
//
//    // TỐI ƯU: Thêm hàm onDestroy để đóng kết nối CSDL, chống rò rỉ bộ nhớ
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (goiMonDAO != null) {
//            goiMonDAO.close();
//        }
//        if (banAnDAO != null) {
//            banAnDAO.close();
//        }
//    }
//
//    // HÀM ĐỊNH DẠNG TIỀN TỆ
//    private String formatVND(long amount) {
//        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//        formatter.applyPattern("#,###");
//        return formatter.format(amount) + " VNĐ";
//    }
//}
//package com.example.anhki.foodapp;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.GridView; // Hoặc ListView/RecyclerView
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager; // Nếu dùng RecyclerView
//import androidx.recyclerview.widget.RecyclerView;    // Nếu dùng RecyclerView
//
//// Firebase Imports
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.ListenerRegistration;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.WriteBatch;
//
//// Import DTO và Adapter mới
//import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiThanhToan; // Adapter đã sửa
//import com.example.anhki.foodapp.DTO.ChiTietGoiMonDTO; // DTO mới
//
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//public class ThanhToanActivity extends AppCompatActivity implements View.OnClickListener {
//    private static final String TAG = "ThanhToanActivity";
//
//    // private GridView gridView; // Bỏ GridView cũ nếu dùng RecyclerView
//    private RecyclerView rvChiTietThanhToan; // Dùng RecyclerView
//    private Button btnThanhToan, btnThoat;
//    private TextView txtTongTien, txtTenBanThanhToan; // Thêm TextView tên bàn
//    private AdapterHienThiThanhToan adapter; // Adapter đã sửa
//    private List<ChiTietGoiMonDTO> chiTietList;
//
//    // Firebase
//    private FirebaseFirestore db;
//    private ListenerRegistration goiMonListener;
//    private ListenerRegistration chiTietListener;
//    private String banAnDocId;
//    private String goiMonDocId; // ID của hóa đơn đang mở
//    private DocumentReference banRef;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_thanhtoan); // Đảm bảo layout dùng RecyclerView
//
//        // Ánh xạ View
//        // gridView = findViewById(R.id.gvThanhToan); // Bỏ
//        rvChiTietThanhToan = findViewById(R.id.rvChiTietThanhToan); // Ánh xạ RecyclerView
//        btnThanhToan = findViewById(R.id.btnThanhToan);
//        btnThoat = findViewById(R.id.btnThoatThanhToan);
//        txtTongTien = findViewById(R.id.txtTongTien);
//        txtTenBanThanhToan = findViewById(R.id.txtTenBanThanhToan); // Ánh xạ tên bàn
//
//        // Khởi tạo Firebase
//        db = FirebaseFirestore.getInstance();
//
//        // Lấy dữ liệu từ Intent
//        banAnDocId = getIntent().getStringExtra("banAnDocId"); // Nhận ID bàn từ Fragment
//        String tenBan = getIntent().getStringExtra("tenBan");    // Nhận tên bàn từ Fragment
//        txtTenBanThanhToan.setText("Bàn: " + tenBan);
//
//        if (banAnDocId == null || banAnDocId.isEmpty()) {
//            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin bàn", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        banRef = db.collection("banAn").document(banAnDocId);
//
//        // Cài đặt RecyclerView
//        chiTietList = new ArrayList<>();
//        adapter = new AdapterHienThiThanhToan(this, R.layout.item_chitiet_goimon, chiTietList); // Dùng layout item mới
//        rvChiTietThanhToan.setLayoutManager(new LinearLayoutManager(this));
//        rvChiTietThanhToan.setAdapter(adapter);
//
//        // Gán sự kiện
//        btnThanhToan.setOnClickListener(this);
//        btnThoat.setOnClickListener(this);
//    }
//
//    // Bắt đầu lắng nghe trong onStart
//    @Override
//    protected void onStart() {
//        super.onStart();
//        findOpenGoiMonAndListen(); // Tìm hóa đơn đang mở và bắt đầu lắng nghe
//    }
//
//    // Dừng lắng nghe trong onStop
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (goiMonListener != null) goiMonListener.remove();
//        if (chiTietListener != null) chiTietListener.remove();
//    }
//
//    // Hàm tìm hóa đơn đang mở và lắng nghe thay đổi
//    private void findOpenGoiMonAndListen() {
//        db.collection("goiMon")
//                .whereEqualTo("maBanRef", banRef)
//                .whereEqualTo("tinhTrang", "false")
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                        DocumentSnapshot goiMonDoc = task.getResult().getDocuments().get(0);
//                        goiMonDocId = goiMonDoc.getId(); // Lưu lại ID hóa đơn
//                        Log.d(TAG,"Tìm thấy goiMon đang mở: " + goiMonDocId);
//                        // Bắt đầu lắng nghe hóa đơn này
//                        listenToGoiMon(goiMonDoc.getReference());
//                        listenToChiTiet(goiMonDoc.getReference());
//                    } else if (task.isSuccessful()) {
//                        Log.w(TAG, "Không tìm thấy goiMon nào đang mở cho bàn: " + banAnDocId);
//                        Toast.makeText(this, "Bàn này không có hóa đơn nào đang mở.", Toast.LENGTH_SHORT).show();
//                        // Có thể vô hiệu hóa nút thanh toán hoặc tự động đóng Activity
//                        btnThanhToan.setEnabled(false);
//                        chiTietList.clear();
//                        adapter.notifyDataSetChanged();
//                        txtTongTien.setText(getString(R.string.tongcong) + " 0 VNĐ");
//                    } else {
//                        Log.e(TAG, "Lỗi tìm kiếm goiMon: ", task.getException());
//                        Toast.makeText(this, "Lỗi tải hóa đơn", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Lắng nghe thay đổi của document goiMon (để cập nhật tổng tiền)
//    private void listenToGoiMon(DocumentReference goiMonRef) {
//        if (goiMonListener != null) goiMonListener.remove(); // Hủy listener cũ
//
//        goiMonListener = goiMonRef.addSnapshotListener((snapshot, e) -> {
//            if (e != null) {
//                Log.w(TAG, "Lỗi lắng nghe goiMon:", e);
//                return;
//            }
//            if (snapshot != null && snapshot.exists()) {
//                Long tongTienLong = snapshot.getLong("tongTien");
//                long tongTienValue = (tongTienLong != null) ? tongTienLong : 0;
//                // Định dạng và hiển thị tổng tiền
//                txtTongTien.setText(getString(R.string.tongcong) + " " + formatVND(tongTienValue));
//                Log.d(TAG,"Tổng tiền cập nhật: "+ formatVND(tongTienValue));
//            } else {
//                Log.d(TAG, "Document goiMon không tồn tại hoặc đã bị xóa");
//                // Hóa đơn có thể đã bị thanh toán hoặc xóa ở thiết bị khác
//                Toast.makeText(this, "Hóa đơn không còn tồn tại", Toast.LENGTH_SHORT).show();
//                btnThanhToan.setEnabled(false);
//            }
//        });
//    }
//
//    // Lắng nghe thay đổi của sub-collection chiTietGoiMon
//    private void listenToChiTiet(DocumentReference goiMonRef) {
//        if (chiTietListener != null) chiTietListener.remove(); // Hủy listener cũ
//
//        chiTietListener = goiMonRef.collection("chiTietGoiMon")
//                .orderBy("tenMonAn", Query.Direction.ASCENDING) // Sắp xếp theo tên món
//                .addSnapshotListener((snapshots, e) -> {
//                    if (e != null) {
//                        Log.w(TAG, "Lỗi lắng nghe chiTietGoiMon:", e);
//                        return;
//                    }
//                    if (snapshots != null) {
//                        chiTietList.clear();
//                        for (QueryDocumentSnapshot doc : snapshots) {
//                            try {
//                                ChiTietGoiMonDTO chiTiet = doc.toObject(ChiTietGoiMonDTO.class);
//                                //chiTiet.setDocumentId(doc.getId()); // Lưu ID nếu cần
//                                chiTietList.add(chiTiet);
//                            } catch (Exception convertError){
//                                Log.e(TAG, "Lỗi convert chi tiết món: " + doc.getId(), convertError);
//                            }
//                        }
//                        adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
//                        Log.d(TAG,"Chi tiết hóa đơn cập nhật: " + chiTietList.size() + " món");
//                    }else {
//                        Log.d(TAG, "Snapshot chiTietGoiMon là null");
//                        chiTietList.clear(); // Xóa list cũ nếu snapshot null
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btnThanhToan) {
//            thucHienThanhToan();
//        } else if (id == R.id.btnThoatThanhToan) {
//            finish();
//        }
//    }
//
//    // Hàm xử lý thanh toán trên Firestore
//    private void thucHienThanhToan() {
//        if (goiMonDocId == null || goiMonDocId.isEmpty()) {
//            Toast.makeText(this, "Không tìm thấy hóa đơn để thanh toán", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        DocumentReference goiMonRef = db.collection("goiMon").document(goiMonDocId);
//
//        // Dùng WriteBatch để cập nhật cả GoiMon và BanAn
//        WriteBatch batch = db.batch();
//
//        // 1. Cập nhật trạng thái GoiMon thành "true" (đã thanh toán)
//        batch.update(goiMonRef, "tinhTrang", "true");
//        // Có thể thêm trường giờ thanh toán: batch.update(goiMonRef, "gioThanhToan", Timestamp.now());
//
//        // 2. Cập nhật trạng thái BanAn thành "false" (trống)
//        batch.update(banRef, "tinhTrang", "false");
//
//        // Thực thi batch
//        batch.commit()
//                .addOnSuccessListener(aVoid -> {
//                    Log.d(TAG, "Thanh toán thành công cho goiMon: " + goiMonDocId);
//                    Toast.makeText(this, getString(R.string.thanhtoanthanhcong), Toast.LENGTH_SHORT).show();
//                    // Listener sẽ tự động cập nhật lại danh sách (thành rỗng) và tổng tiền
//                    btnThanhToan.setEnabled(false); // Vô hiệu hóa nút sau khi thanh toán
//                    // finish(); // Có thể đóng Activity sau khi thanh toán
//                })
//                .addOnFailureListener(e -> {
//                    Log.w(TAG, "Lỗi khi thanh toán goiMon: " + goiMonDocId, e);
//                    Toast.makeText(this, getString(R.string.loi), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//
//    // Hàm định dạng tiền tệ (giữ nguyên)
//    private String formatVND(long amount) {
//        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//        formatter.applyPattern("#,###");
//        return formatter.format(amount) + " VNĐ";
//    }

    // Bỏ onDestroy vì không còn DAO cần đóng
    // @Override
    // protected void onDestroy() { ... }
//}
package com.example.anhki.foodapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
// Bỏ GridView nếu không dùng
// import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Import RecyclerView
import androidx.recyclerview.widget.RecyclerView;    // Import RecyclerView

// Firebase Imports
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot; // Import QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

// Import DTO và Adapter mới
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiThanhToan; // Adapter RecyclerView
import com.example.anhki.foodapp.DTO.ChiTietGoiMonDTO; // DTO mới

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ThanhToanActivity extends AppCompatActivity implements View.OnClickListener {
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
        // goiMonDocId cũng có thể được gửi từ Fragment nếu muốn,
        // nhưng tìm lại trong onStart an toàn hơn phòng trường hợp dữ liệu thay đổi.
        // goiMonDocId = getIntent().getStringExtra("goiMonDocId");

        // Hiển thị tên bàn
        if (tenBan != null && !tenBan.isEmpty()) {
            txtTenBanThanhToan.setText("Bàn: " + tenBan);
        } else {
            txtTenBanThanhToan.setText("Chi tiết hóa đơn"); // Mặc định nếu không có tên bàn
        }

        // Kiểm tra ID bàn
        if (banAnDocId == null || banAnDocId.isEmpty()) {
            Log.e(TAG, "Lỗi: Không nhận được banAnDocId từ Intent.");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin bàn", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu thiếu ID bàn
            return;
        }
        banRef = db.collection("banAn").document(banAnDocId);

        // Cài đặt RecyclerView
        chiTietList = new ArrayList<>();
        // Sử dụng Adapter RecyclerView và layout item_chitiet_goimon.xml
        adapter = new AdapterHienThiThanhToan(this, R.layout.item_chitiet_goimon, chiTietList);
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
        // Hủy listener cũ nếu đang chạy
        if (chiTietListener != null) chiTietListener.remove();

        chiTietListener = goiMonRef.collection("chiTietGoiMon")
                .orderBy("tenMonAn", Query.Direction.ASCENDING) // Sắp xếp cho dễ nhìn
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Lỗi lắng nghe chiTietGoiMon:", e);
                        return; // Dừng nếu có lỗi
                    }

                    if (snapshots != null) {
                        chiTietList.clear(); // Xóa sạch danh sách cũ
                        Log.d(TAG, "Có " + snapshots.size() + " món trong chi tiết.");
                        for (QueryDocumentSnapshot doc : snapshots) {
                            try {
                                // Chuyển document thành đối tượng DTO
                                ChiTietGoiMonDTO chiTiet = doc.toObject(ChiTietGoiMonDTO.class);
                                // chiTiet.setDocumentId(doc.getId()); // Lưu ID document nếu cần dùng sau này
                                chiTietList.add(chiTiet); // Thêm vào danh sách
                            } catch (Exception convertError){
                                // Lỗi này thường do tên trường Firestore không khớp DTO
                                Log.e(TAG, "Lỗi convert chi tiết món: " + doc.getId(), convertError);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                        Log.d(TAG,"Chi tiết hóa đơn hiển thị cập nhật: " + chiTietList.size() + " món");
                    } else {
                        Log.d(TAG, "Snapshot chiTietGoiMon là null");
                        chiTietList.clear(); // Xóa nếu snapshot null
                        adapter.notifyDataSetChanged();
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
        // Có thể thêm các trường khác nếu cần, ví dụ:
        // batch.update(goiMonRef, "thoiGianThanhToan", Timestamp.now());
        // batch.update(goiMonRef, "nhanVienThanhToan", FirebaseAuth.getInstance().getCurrentUser().getUid());

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

    // Không cần hàm onDestroy để đóng DAO nữa
}