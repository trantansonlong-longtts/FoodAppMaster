package com.example.anhki.foodapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

// Firebase imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.DTO.QuyenDTO;

import java.util.ArrayList;
import java.util.HashMap; // Thêm import
import java.util.List;
import java.util.Map;

public class DangKyActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DangKyActivity";
    private EditText edTenDangNhap, edMatKhau, edNgaySinh, edCCCD;
    private Button btnDongY, btnThoat;
    private TextView txtTieuDeDangKy;
    private RadioGroup rgGioiTinh;
    private Spinner spinQuyen;

    private int manhanvien = 0;

    private List<QuyenDTO> quyenDTOList;
    private boolean laQuanLyDauTien =false;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangky);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        anhXaView();

        // Thiết lập listener
        btnDongY.setOnClickListener(this);
        btnThoat.setOnClickListener(this);
        edNgaySinh.setOnClickListener(this);

        // Hiển thị danh sách quyền
        hienThiDanhSachQuyen();

        // Kiểm tra xem đang sửa hay thêm mới
        manhanvien = getIntent().getIntExtra("manhanvien", 0);
        if (manhanvien != 0) {
            txtTieuDeDangKy.setText(R.string.capnhatnhanvien);
            // Vô hiệu hóa nút nếu là sửa (logic sửa chưa được làm)
            btnDongY.setEnabled(false);
            Toast.makeText(this, "Chức năng Sửa chưa được hỗ trợ với Firebase", Toast.LENGTH_SHORT).show();
            //hienThiThongTinNhanVien();
        }

        // Xử lý Quản lý đầu tiên (Giữ nguyên)
        laQuanLyDauTien = getIntent().getBooleanExtra("laQuanLyDauTien", false);
        if (laQuanLyDauTien) {
            txtTieuDeDangKy.setText("Tạo tài khoản Quản lý");
            spinQuyen.setVisibility(View.GONE); // Ẩn Spinner đi
        }
    }

    private void anhXaView() {
        edTenDangNhap = findViewById(R.id.edTenDangNhapDK);
        edMatKhau = findViewById(R.id.edMatKhauDK);
        edNgaySinh = findViewById(R.id.edNgaySinhDK);
        edCCCD = findViewById(R.id.edCCCDDK);
        rgGioiTinh = findViewById(R.id.rgGioiTinhDK);
        spinQuyen = findViewById(R.id.spinQuyen);
        txtTieuDeDangKy = findViewById(R.id.txtTieuDeDangKy);
        btnDongY = findViewById(R.id.btnDongYDK);
        btnThoat = findViewById(R.id.btnThoatDK);
    }

    private void hienThiDanhSachQuyen() {
        quyenDTOList = new ArrayList<>(); // Khởi tạo list
        db.collection("quyen") // db giờ đã được khởi tạo
                .orderBy("tenQuyen")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> dataAdapter = new ArrayList<>();
                        quyenDTOList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuyenDTO quyen = document.toObject(QuyenDTO.class);
                            quyen.setMaQuyen(Integer.parseInt(document.getId())); // Lấy ID document làm mã quyền
                            quyenDTOList.add(quyen);
                            dataAdapter.add(quyen.getTenQuyen());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dataAdapter);
                        spinQuyen.setAdapter(adapter);

                        if (laQuanLyDauTien) {
                            spinQuyen.setSelection(dataAdapter.indexOf("Quản lý"));
                            spinQuyen.setEnabled(false);
                        }
                    } else {
                        Log.w(TAG, "Lỗi lấy danh sách quyền.", task.getException());
                        Toast.makeText(this, "Lỗi tải danh sách quyền", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // --- SỬA LẠI: ĐỔI TÊN HÀM VÀ THÊM LOGIC FIREBASE ---
    private void dangKyTaiKhoan() {
        String email = edTenDangNhap.getText().toString().trim(); // Dùng Tên đăng nhập làm Email
        String matKhau = edMatKhau.getText().toString().trim();
        String CCCD = edCCCD.getText().toString().trim();
        String ngaySinh = edNgaySinh.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(matKhau)) {
            Toast.makeText(this, "Email và Mật khẩu không được trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgGioiTinh.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }
        String gioiTinh = (rgGioiTinh.getCheckedRadioButtonId() == R.id.rdNam) ? "Nam" : "Nữ";

        // Lấy mã quyền
        int maQuyen;
        if (laQuanLyDauTien) {
            maQuyen = Contants.QUYEN_QUANLY;
        } else {
            if (quyenDTOList == null || quyenDTOList.isEmpty()) {
                Toast.makeText(this, "Lỗi: Không tải được danh sách quyền", Toast.LENGTH_SHORT).show();
                return;
            }
            int vitri = spinQuyen.getSelectedItemPosition();
            maQuyen = quyenDTOList.get(vitri).getMaQuyen();
        }

        // Bắt đầu tạo tài khoản trên Firebase
        mAuth.createUserWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Tạo tài khoản Auth thành công.");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            luuThongTinNhanVienVaoFirestore(user.getUid(), email, CCCD, ngaySinh, gioiTinh, maQuyen);
                        }
                    } else {
                        Log.w(TAG, "Tạo tài khoản Auth thất bại", task.getException());
                        Toast.makeText(DangKyActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Hàm lưu thông tin nhân viên lên Firestore
    private void luuThongTinNhanVienVaoFirestore(String uid, String email, String CCCD, String ngaySinh, String gioiTinh, int maQuyen) {
        Map<String, Object> nhanVienData = new HashMap<>();
        nhanVienData.put("tenDangNhap", email);
        nhanVienData.put("CCCD", CCCD);
        nhanVienData.put("ngaySinh", ngaySinh);
        nhanVienData.put("gioiTinh", gioiTinh);
        nhanVienData.put("maQuyen", (long) maQuyen); // Lưu dưới dạng Long (Number) trên Firestore

        db.collection("nhanVien").document(uid)
                .set(nhanVienData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Lưu thông tin nhân viên vào Firestore thành công!");
                    Toast.makeText(DangKyActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi lưu thông tin vào Firestore", e);
                    Toast.makeText(DangKyActivity.this, "Lỗi lưu thông tin bổ sung.", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("NewApi")
    private void chooseDay() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edNgaySinh.setText(date);
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnDongYDK) {
            dangKyTaiKhoan(); // Gọi hàm đăng ký Firebase
        } else if (id == R.id.btnThoatDK) {
            finish();
        } else if (id == R.id.edNgaySinhDK) {
            chooseDay();
        }
    }


}
