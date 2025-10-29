package com.example.anhki.foodapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // Nếu cần hiển thị tên món
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import android.widget.ImageButton; // Thêm import
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SoLuongActivity extends AppCompatActivity {
    private static final String TAG = "SoLuongActivity";
    private ImageButton btnGiam, btnTang; // Thêm 2 nút
    private int currentSoLuong = 1; // Biến lưu số lượng hiện tại
    private EditText edSoLuong;
    private Button btnDongYSoLuong;
    private TextView txtTenMonDisplay; // TextView hiển thị tên món

    // Firebase
    private FirebaseFirestore db;

    // Dữ liệu nhận từ Fragment
    private String monAnDocId;
    private String banAnDocId;
    private String maNhanVienUid;

    // Thông tin món ăn (lấy từ Firestore)
    private String tenMonAn;
    private long giaTien; // Dùng long để tránh lỗi với số lớn

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_themsoluong); // Đảm bảo đúng layout

        edSoLuong = findViewById(R.id.edSoLuongMonAn);
        btnDongYSoLuong = findViewById(R.id.btnDongYSoLuong);
        txtTenMonDisplay = findViewById(R.id.txtTenMonDisplay); // Ánh xạ TextView tên món
        btnGiam = findViewById(R.id.btnGiamSoLuong); // Ánh xạ nút giảm
        btnTang = findViewById(R.id.btnTangSoLuong); // Ánh xạ nút tăng

        db = FirebaseFirestore.getInstance();

        // Khởi tạo giá trị ban đầu và gán sự kiện
        edSoLuong.setText(String.valueOf(currentSoLuong));
        btnGiam.setOnClickListener(v -> updateSoLuong(-1));
        btnTang.setOnClickListener(v -> updateSoLuong(1));

        // Nhận dữ liệu từ Intent
        monAnDocId = getIntent().getStringExtra("monAnDocId");
        banAnDocId = getIntent().getStringExtra("banAnDocId");
        maNhanVienUid = getIntent().getStringExtra("maNhanVienUid");

        if (monAnDocId == null || banAnDocId == null || maNhanVienUid == null) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin món ăn hoặc bàn ăn.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải thông tin món ăn (tên, giá) từ Firestore
        loadMonAnData();

        btnDongYSoLuong.setOnClickListener(v -> xuLyGoiMonVoiSoLuong());
    }

    // Hàm cập nhật số lượng khi nhấn +/-
    private void updateSoLuong(int change) {
        currentSoLuong += change;
        if (currentSoLuong < 1) { // Không cho phép số lượng < 1
            currentSoLuong = 1;
        }
        edSoLuong.setText(String.valueOf(currentSoLuong));
    }
    private void loadMonAnData() {
        db.collection("monAn").document(monAnDocId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tenMonAn = doc.getString("tenMonAn");
                        Long gia = doc.getLong("giaTien");
                        giaTien = (gia != null) ? gia : 0;
                        txtTenMonDisplay.setText("Món: " + tenMonAn); // Hiển thị tên món
                    } else {
                        Toast.makeText(this, "Lỗi: Không tìm thấy món ăn.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải thông tin món ăn.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void xuLyGoiMonVoiSoLuong() {
        // Đọc số lượng từ EditText để bắt trường hợp người dùng sửa trực tiếp
        try {
            int finalSoLuong = Integer.parseInt(edSoLuong.getText().toString());
            if (finalSoLuong <= 0) {
                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
            currentSoLuong = finalSoLuong; // Cập nhật lại biến nếu người dùng sửa tay
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tenMonAn == null) {
            Toast.makeText(this, "Đang tải thông tin món...", Toast.LENGTH_SHORT).show();
            return; // Chưa tải xong tên/giá
        }
        int soLuong = currentSoLuong;

        DocumentReference banRef = db.collection("banAn").document(banAnDocId);
        DocumentReference monAnRef = db.collection("monAn").document(monAnDocId);
        String monAnDocIdTrongChiTiet = monAnDocId;

        // Tìm hóa đơn đang mở ("false") cho bàn này
        db.collection("goiMon")
                .whereEqualTo("maBanRef", banRef)
                .whereEqualTo("tinhTrang", "false")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Đã tìm thấy hóa đơn -> Thêm/Cập nhật món vào hóa đơn đó
                        DocumentSnapshot goiMonDoc = task.getResult().getDocuments().get(0);
                        themHoacCapNhatMonTrongGoiMon(goiMonDoc.getReference(), monAnRef, monAnDocIdTrongChiTiet, soLuong);
                    } else {
                        // Lỗi không tìm thấy hóa đơn (trường hợp hiếm gặp nếu logic trước đúng)
                        Log.e(TAG, "Lỗi nghiêm trọng: Không tìm thấy goiMon đang mở cho bàn " + banAnDocId, task.getException());
                        Toast.makeText(this, "Lỗi hệ thống khi gọi món!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//
//    private void taoGoiMonMoiVaThemMon(DocumentReference banRef, DocumentReference monAnRef, String monAnDocIdTrongChiTiet, int soLuong) {
//        long thanhTien = giaTien * soLuong;
//
//        Map<String, Object> goiMonData = new HashMap<>();
//        goiMonData.put("maBanRef", banRef);
//        goiMonData.put("maNhanVien", maNhanVienUid);
//        goiMonData.put("ngayGoi", Timestamp.now());
//        goiMonData.put("tinhTrang", "false");
//        goiMonData.put("tongTien", thanhTien); // Tổng tiền ban đầu
//
//        Map<String, Object> chiTietData = new HashMap<>();
//        chiTietData.put("maMonAnRef", monAnRef);
//        chiTietData.put("tenMonAn", tenMonAn);
//        chiTietData.put("giaTien", giaTien);
//        chiTietData.put("soLuong", soLuong);
//
//        WriteBatch batch = db.batch();
//        DocumentReference newGoiMonRef = db.collection("goiMon").document();
//        batch.set(newGoiMonRef, goiMonData);
//        DocumentReference chiTietRef = newGoiMonRef.collection("chiTietGoiMon").document(monAnDocIdTrongChiTiet);
//        batch.set(chiTietRef, chiTietData);
//        batch.update(banRef, "tinhTrang", "true");
//
//        batch.commit()
//                .addOnSuccessListener(aVoid -> {
//                    Log.d(TAG, "Tạo gọi món mới và thêm " + soLuong + " " + tenMonAn + " thành công!");
//                    Toast.makeText(this, "Đã thêm: " + soLuong + " " + tenMonAn, Toast.LENGTH_SHORT).show();
//                    setResult(Activity.RESULT_OK); // Báo thành công về Fragment
//                    finish(); // Đóng Activity SoLuong
//                })
//                .addOnFailureListener(e -> {
//                    Log.w(TAG, "Lỗi tạo gọi món mới", e);
//                    Toast.makeText(this, "Lỗi khi thêm món", Toast.LENGTH_SHORT).show();
//                });
//    }

    private void themHoacCapNhatMonTrongGoiMon(DocumentReference goiMonRef, DocumentReference monAnRef, String monAnDocIdTrongChiTiet, int soLuongThem) {
        DocumentReference chiTietRef = goiMonRef.collection("chiTietGoiMon").document(monAnDocIdTrongChiTiet);

        db.runTransaction(transaction -> {
            DocumentSnapshot chiTietSnap = transaction.get(chiTietRef);
            DocumentSnapshot goiMonSnap = transaction.get(goiMonRef); // Đọc cả GoiMon để lấy tổng tiền cũ (nếu cần)

            long soLuongMoi;
            long giaMon = giaTien; // Lấy giá món đã load
            long thayDoiTongTien = giaMon * soLuongThem; // Số tiền tăng thêm

            if (chiTietSnap.exists()) {
                // Món đã có -> Cộng thêm số lượng
                Long soLuongHienTai = chiTietSnap.getLong("soLuong");
                soLuongMoi = (soLuongHienTai != null ? soLuongHienTai : 0) + soLuongThem;
                transaction.update(chiTietRef, "soLuong", soLuongMoi);
            } else {
                // Món chưa có -> Thêm mới vào chi tiết
                soLuongMoi = soLuongThem;
                Map<String, Object> chiTietData = new HashMap<>();
                chiTietData.put("maMonAnRef", monAnRef);
                chiTietData.put("tenMonAn", tenMonAn);
                chiTietData.put("giaTien", giaMon);
                chiTietData.put("soLuong", soLuongMoi);
                transaction.set(chiTietRef, chiTietData);
            }

            // Cập nhật tổng tiền của hóa đơn
            transaction.update(goiMonRef, "tongTien", FieldValue.increment(thayDoiTongTien));

            return soLuongMoi;
        }).addOnSuccessListener(result -> {
            Log.d(TAG, "Thêm/Cập nhật " + soLuongThem + " " + tenMonAn + " thành công, SL mới: " + result);
            Toast.makeText(this, "Đã thêm: " + soLuongThem + " " + tenMonAn, Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Lỗi Transaction thêm/cập nhật món", e);
            Toast.makeText(this, "Lỗi khi thêm món", Toast.LENGTH_SHORT).show();
        });
    }
}