package com.example.anhki.foodapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Import
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class SoLuongActivity extends AppCompatActivity {
    private static final String TAG = "SoLuongActivity";

    private EditText edSoLuong;
    private Button btnDongYSoLuong;
    private TextView txtTenMonDisplay;
    private ImageButton btnGiam, btnTang;

    private FirebaseFirestore db;
    private int currentSoLuong = 1; // Số lượng mặc định

    // Biến cho cả 2 chế độ
    private String tenMonAn;
    private long giaTien;

    // Biến cho chế độ THÊM MỚI
    private String monAnDocId;
    private String banAnDocId;
    private String maNhanVienUid;

    // Biến cho chế độ SỬA
    private String goiMonDocId;
    private String chiTietDocId;
    private long soLuongCu; // Số lượng ban đầu khi mở màn hình sửa
    private boolean isEditMode = false;

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
        // Phân biệt chế độ Sửa hay Thêm
        goiMonDocId = getIntent().getStringExtra("goiMonDocId");
        chiTietDocId = getIntent().getStringExtra("chiTietDocId");
        if (goiMonDocId != null && chiTietDocId != null) {
            // === CHẾ ĐỘ SỬA ===
            isEditMode = true;
            soLuongCu = getIntent().getLongExtra("currentSoLuong", 0);
            giaTien = getIntent().getLongExtra("giaTien", 0);
            tenMonAn = getIntent().getStringExtra("tenMonAn");

            txtTenMonDisplay.setText("Sửa: " + tenMonAn);
            currentSoLuong = (int) soLuongCu;
            edSoLuong.setText(String.valueOf(currentSoLuong));

        } else {
            // === CHẾ ĐỘ THÊM MỚI ===
            isEditMode = false;
            monAnDocId = getIntent().getStringExtra("monAnDocId");
            banAnDocId = getIntent().getStringExtra("banAnDocId");
            maNhanVienUid = getIntent().getStringExtra("maNhanVienUid");

            if (monAnDocId == null || banAnDocId == null || maNhanVienUid == null) {
                Toast.makeText(this, "Lỗi: Thiếu thông tin.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            loadMonAnData(monAnDocId); // Tải tên, giá
            edSoLuong.setText(String.valueOf(currentSoLuong));
        }
        // Gán sự kiện cho nút +/-
        btnGiam.setOnClickListener(v -> updateSoLuong(-1));
        btnTang.setOnClickListener(v -> updateSoLuong(1));
        btnDongYSoLuong.setOnClickListener(v -> dongYXacNhan());
//
//        // Khởi tạo giá trị ban đầu và gán sự kiện
//        edSoLuong.setText(String.valueOf(currentSoLuong));
//        btnGiam.setOnClickListener(v -> updateSoLuong(-1));
//        btnTang.setOnClickListener(v -> updateSoLuong(1));
//
//        // Nhận dữ liệu từ Intent
//        monAnDocId = getIntent().getStringExtra("monAnDocId");
//        banAnDocId = getIntent().getStringExtra("banAnDocId");
//        maNhanVienUid = getIntent().getStringExtra("maNhanVienUid");
//
//        if (monAnDocId == null || banAnDocId == null || maNhanVienUid == null) {
//            Toast.makeText(this, "Lỗi: Thiếu thông tin món ăn hoặc bàn ăn.", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Tải thông tin món ăn (tên, giá) từ Firestore
//        loadMonAnData();
//
//        btnDongYSoLuong.setOnClickListener(v -> xuLyGoiMonVoiSoLuong());
    }
    // Tải thông tin món (chỉ dùng cho chế độ Thêm mới)
    private void loadMonAnData(String monAnDocId) {
        db.collection("monAn").document(monAnDocId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tenMonAn = doc.getString("tenMonAn");
                        Long gia = doc.getLong("giaTien");
                        giaTien = (gia != null) ? gia : 0;
                        txtTenMonDisplay.setText("Món: " + tenMonAn);
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
    // Hàm cập nhật số lượng khi nhấn +/-
    // Hàm cập nhật số lượng khi nhấn +/-
    private void updateSoLuong(int change) {
        currentSoLuong += change;
        if (currentSoLuong < (isEditMode ? 0 : 1)) { // Nếu đang sửa, cho phép về 0 (để xóa)
            currentSoLuong = (isEditMode ? 0 : 1);
        }
        edSoLuong.setText(String.valueOf(currentSoLuong));
    }
    // Hàm khi nhấn nút "Đồng ý"
    private void dongYXacNhan() {
        int soLuongMoi;
        try {
            soLuongMoi = Integer.parseInt(edSoLuong.getText().toString());
            if (soLuongMoi < (isEditMode ? 0 : 1)) {
                String message = isEditMode ? "Số lượng không thể âm" : "Số lượng phải lớn hơn 0";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            xuLySuaSoLuong(soLuongMoi);
        } else {
            xuLyGoiMonVoiSoLuong(soLuongMoi);
        }
    }

    // Hàm xử lý SỬA số lượng
    private void xuLySuaSoLuong(int soLuongMoi) {
        DocumentReference goiMonRef = db.collection("goiMon").document(goiMonDocId);
        DocumentReference chiTietRef = goiMonRef.collection("chiTietGoiMon").document(chiTietDocId);

        // Tính toán chênh lệch
        long soLuongChenhLech = soLuongMoi - soLuongCu;
        long tienChenhLech = giaTien * soLuongChenhLech;

        WriteBatch batch = db.batch();
        if (soLuongMoi == 0) {
            // Nếu số lượng mới là 0 -> Xóa món
            batch.delete(chiTietRef);
        } else {
            // Nếu số lượng > 0 -> Cập nhật số lượng
            batch.update(chiTietRef, "soLuong", soLuongMoi);
        }
        // Cập nhật tổng tiền
        batch.update(goiMonRef, "tongTien", FieldValue.increment(tienChenhLech));

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, (soLuongMoi == 0 ? "Xóa món" : "Cập nhật") + " thành công", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Lỗi cập nhật số lượng: " + chiTietDocId, e);
                });
    }

    // Hàm xử lý THÊM MỚI món
    private void xuLyGoiMonVoiSoLuong(int soLuongThem) {
        DocumentReference banRef = db.collection("banAn").document(banAnDocId);
        DocumentReference monAnRef = db.collection("monAn").document(monAnDocId);
        String monAnDocIdTrongChiTiet = monAnDocId;

        // Tìm hóa đơn đang mở
        db.collection("goiMon")
                .whereEqualTo("maBanRef", banRef)
                .whereEqualTo("tinhTrang", "false")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // CHƯA CÓ -> Tạo mới
                            taoGoiMonMoiVaThemMon(banRef, monAnRef, monAnDocIdTrongChiTiet, soLuongThem);
                        } else {
                            // ĐÃ CÓ -> Cập nhật
                            DocumentSnapshot goiMonDoc = task.getResult().getDocuments().get(0);
                            themHoacCapNhatMonTrongGoiMon(goiMonDoc.getReference(), monAnRef, monAnDocIdTrongChiTiet, soLuongThem);
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi gọi món", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void taoGoiMonMoiVaThemMon(DocumentReference banRef, DocumentReference monAnRef, String monAnDocIdTrongChiTiet, int soLuong) {
        // tenMonAn và giaTien là biến toàn cục của Activity, đã được load trong loadMonAnData()
        long thanhTien = giaTien * soLuong;

        // 1. Tạo dữ liệu cho Hóa đơn mới (goiMon)
        Map<String, Object> goiMonData = new HashMap<>();
        goiMonData.put("maBanRef", banRef);
        goiMonData.put("maNhanVien", maNhanVienUid); // Biến toàn cục đã lấy từ Intent
        goiMonData.put("ngayGoi", Timestamp.now());
        goiMonData.put("tinhTrang", "false"); // "false" = chưa thanh toán
        goiMonData.put("tongTien", thanhTien); // Tổng tiền của món đầu tiên

        // 2. Tạo dữ liệu cho Chi tiết món đầu tiên (chiTietGoiMon)
        Map<String, Object> chiTietData = new HashMap<>();
        chiTietData.put("maMonAnRef", monAnRef);
        chiTietData.put("tenMonAn", tenMonAn); // Biến toàn cục
        chiTietData.put("giaTien", giaTien); // Biến toàn cục
        chiTietData.put("soLuong", soLuong);

        // 3. Sử dụng WriteBatch để thực hiện 3 thao tác cùng lúc
        WriteBatch batch = db.batch();

        // Thao tác 1: Tạo document GoiMon mới
        DocumentReference newGoiMonRef = db.collection("goiMon").document(); // Tự tạo ID
        batch.set(newGoiMonRef, goiMonData);

        // Thao tác 2: Thêm document ChiTietGoiMon vào sub-collection
        DocumentReference chiTietRef = newGoiMonRef.collection("chiTietGoiMon").document(monAnDocIdTrongChiTiet);
        batch.set(chiTietRef, chiTietData);

        // Thao tác 3: Cập nhật trạng thái bàn ăn thành "true" (có khách)
        batch.update(banRef, "tinhTrang", "true");

        // 4. Thực thi batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Tạo gọi món mới và thêm " + soLuong + " " + tenMonAn + " thành công!");
                    Toast.makeText(this, "Đã thêm: " + soLuong + " " + tenMonAn, Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK); // Báo thành công
                    finish(); // Đóng Activity SoLuong
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi tạo gọi món mới", e);
                    Toast.makeText(this, "Lỗi khi thêm món", Toast.LENGTH_SHORT).show();
                });
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


    private void themHoacCapNhatMonTrongGoiMon(DocumentReference goiMonRef, DocumentReference monAnRef, String monAnDocIdTrongChiTiet, int soLuongThem) {
        // Reference đến document chi tiết món ăn (có thể đã tồn tại hoặc chưa)
        DocumentReference chiTietRef = goiMonRef.collection("chiTietGoiMon").document(monAnDocIdTrongChiTiet);

        // Tính toán số tiền sẽ được cộng thêm vào tổng hóa đơn
        long tienChenhLech = giaTien * soLuongThem;

        // 1. Dùng Transaction để đảm bảo tính toàn vẹn dữ liệu
        db.runTransaction(transaction -> {
            DocumentSnapshot chiTietSnap = transaction.get(chiTietRef);

            long soLuongMoi;
            if (chiTietSnap.exists()) {
                // MÓN ĐÃ CÓ -> Lấy số lượng cũ, cộng thêm số lượng mới
                Long soLuongHienTai = chiTietSnap.getLong("soLuong");
                soLuongMoi = (soLuongHienTai != null ? soLuongHienTai : 0) + soLuongThem;
                // Cập nhật lại số lượng
                transaction.update(chiTietRef, "soLuong", soLuongMoi);
            } else {
                // MÓN CHƯA CÓ -> Thêm mới vào chi tiết
                soLuongMoi = soLuongThem;
                Map<String, Object> chiTietData = new HashMap<>();
                chiTietData.put("maMonAnRef", monAnRef);
                chiTietData.put("tenMonAn", tenMonAn);
                chiTietData.put("giaTien", giaTien);
                chiTietData.put("soLuong", soLuongMoi);
                transaction.set(chiTietRef, chiTietData);
            }

            // 2. Luôn cập nhật (cộng dồn) tổng tiền của hóa đơn
            transaction.update(goiMonRef, "tongTien", FieldValue.increment(tienChenhLech));

            return soLuongMoi; // Trả về số lượng mới để hiển thị (tùy chọn)
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