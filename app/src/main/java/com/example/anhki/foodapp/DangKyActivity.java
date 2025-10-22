package com.example.anhki.foodapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

//import com.example.anhki.foodapp.DAO.NhanVienDAO;
import com.example.anhki.foodapp.DAO.QuyenDAO;
//import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.DTO.QuyenDTO;
//import com.example.anhki.foodapp.Contants;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DangKyActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DangKyActivity"; // Thêm TAG để debug
    private EditText edTenDangNhap, edMatKhau, edNgaySinh, edCMND;
    private Button btnDongY, btnThoat;
    private TextView txtTieuDeDangKy;
    private RadioGroup rgGioiTinh;
    private Spinner spinQuyen;

    private int manhanvien = 0; // =0 là thêm mới, !=0 là cập nhật
    private boolean laQuanLyDauTien = false;
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    //private NhanVienDAO nhanVienDAO;
    private QuyenDAO quyenDAO;
    private List<QuyenDTO> quyenDTOList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangky);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Khởi tạo DAO
        //nhanVienDAO = new NhanVienDAO(this);
        quyenDAO = new QuyenDAO(this);

        // Ánh xạ View
        anhXaView();

        // Thiết lập listener
        btnDongY.setOnClickListener(this);
        btnThoat.setOnClickListener(this);
        edNgaySinh.setOnClickListener(this);

        // Hiển thị danh sách quyền
        hienThiDanhSachQuyen();

        laQuanLyDauTien = getIntent().getBooleanExtra("laQuanLyDauTien", false);
        if (laQuanLyDauTien) {
            // Nếu là Quản lý đầu tiên, ẩn Spinner đi và mặc định quyền là Quản lý
            //spinQuyen.setVisibility(View.GONE);
            txtTieuDeDangKy.setText("Tạo tài khoản Quản lý");
            // Khóa Spinner lại, mặc định chọn Quản lý
            spinQuyen.setSelection(0); // Giả sử Quản lý luôn ở vị trí đầu tiên
            spinQuyen.setEnabled(false);

        }
        // Chức năng sửa sẽ phức tạp hơn, tạm thời chỉ tập trung vào Đăng ký mới
        manhanvien = getIntent().getIntExtra("manhanvien", 0);
        if (manhanvien != 0) {
            Toast.makeText(this, "Chức năng sửa chưa hỗ trợ Firebase!", Toast.LENGTH_SHORT).show();
            btnDongY.setEnabled(false); // Vô hiệu hóa nút Đồng ý nếu là sửa
        }
    }

    private void anhXaView() {
        edTenDangNhap = findViewById(R.id.edTenDangNhapDK);
        edMatKhau = findViewById(R.id.edMatKhauDK);
        edNgaySinh = findViewById(R.id.edNgaySinhDK);
        edCMND = findViewById(R.id.edCMNDDK);
        rgGioiTinh = findViewById(R.id.rgGioiTinhDK);
        spinQuyen = findViewById(R.id.spinQuyen);
        txtTieuDeDangKy = findViewById(R.id.txtTieuDeDangKy);
        btnDongY = findViewById(R.id.btnDongYDK);
        btnThoat = findViewById(R.id.btnThoatDK);
    }

    private void hienThiDanhSachQuyen() {
        quyenDTOList = quyenDAO.LayDanhSachQuyen();
        List<String> dataAdapter = new ArrayList<>();
        for (QuyenDTO quyen : quyenDTOList) {
            dataAdapter.add(quyen.getTenQuyen());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dataAdapter);
        spinQuyen.setAdapter(adapter);
    }


    private void dangKyTaiKhoan() {
        String email = edTenDangNhap.getText().toString().trim(); // Dùng Tên đăng nhập làm Email
        String password = edMatKhau.getText().toString().trim();
        String cmnd = edCMND.getText().toString().trim();
        String ngaySinh = edNgaySinh.getText().toString().trim();
        String gioiTinh = (rgGioiTinh.getCheckedRadioButtonId() == R.id.rdNam) ? "Nam" : "Nữ";

        // Validate
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email và Mật khẩu không được trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgGioiTinh.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiện loading indicator (nếu có)
        // ...

        // 1. Tạo tài khoản trên Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký Auth thành công
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            // 2. Lưu thông tin bổ sung vào Firestore
                            luuThongTinNhanVienVaoFirestore(uid, email, cmnd, ngaySinh, gioiTinh);
                        }
                    } else {
                        // Đăng ký Auth thất bại
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(DangKyActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        // Ẩn loading indicator
                    }
                });
    }

    private void luuThongTinNhanVienVaoFirestore(String uid, String email, String cmnd, String ngaySinh, String gioiTinh) {
        int maQuyen;
        if (laQuanLyDauTien) {
            maQuyen = Contants.QUYEN_QUANLY;
        } else {
            int vitri = spinQuyen.getSelectedItemPosition();
            maQuyen = (quyenDTOList != null && !quyenDTOList.isEmpty()) ? quyenDTOList.get(vitri).getMaQuyen() : Contants.QUYEN_NHANVIEN; // Mặc định là Nhân viên nếu có lỗi
        }

        // Tạo một Map để lưu vào Firestore (linh hoạt hơn DTO)
        Map<String, Object> nhanVienData = new HashMap<>();
        nhanVienData.put("tenDangNhap", email); // Lưu lại email/tên đăng nhập
        nhanVienData.put("cmnd", cmnd);
        nhanVienData.put("ngaySinh", ngaySinh);
        nhanVienData.put("gioiTinh", gioiTinh);
        nhanVienData.put("maQuyen", maQuyen);

        // Lưu vào collection "nhanVien" với document ID là UID của user
        db.collection("nhanVien").document(uid)
                .set(nhanVienData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Lưu thông tin nhân viên vào Firestore thành công!");
                    Toast.makeText(DangKyActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish(); // Đóng màn hình đăng ký
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi lưu thông tin vào Firestore", e);
                    Toast.makeText(DangKyActivity.this, "Lỗi lưu thông tin bổ sung.", Toast.LENGTH_SHORT).show();
                    // Có thể cân nhắc xóa tài khoản Auth đã tạo nếu lưu Firestore lỗi? (logic phức tạp hơn)
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
            // Thay vì gọi dongY() cũ, gọi hàm đăng ký Firebase mới
            dangKyTaiKhoan();
        } else if (id == R.id.btnThoatDK) {
            finish();
        } else if (id == R.id.edNgaySinhDK) {
            chooseDay();
        }
    }

    // CẢI TIẾN: Đóng kết nối DAO để chống rò rỉ bộ nhớ
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (quyenDAO != null) {
            quyenDAO.close();
        }
    }
}
