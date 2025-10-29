package com.example.anhki.foodapp;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.DTO.QuyenDTO;

import java.util.ArrayList;
import java.util.List;

public class DangKyActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DangKyActivity";
    private EditText edTenDangNhap, edMatKhau, edNgaySinh, edCMND;
    private Button btnDongY, btnThoat;
    private TextView txtTieuDeDangKy;
    private RadioGroup rgGioiTinh;
    private Spinner spinQuyen;

    private int manhanvien = 0;

    private List<QuyenDTO> quyenDTOList;
    private boolean laQuanLyDauTien;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangky);

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
            //hienThiThongTinNhanVien();
        }

        laQuanLyDauTien = getIntent().getBooleanExtra("laQuanLyDauTien", false);
        if (laQuanLyDauTien) {
            // Nếu là Quản lý đầu tiên, ẩn Spinner đi và mặc định quyền là Quản lý
            spinQuyen.setVisibility(View.GONE);

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
        quyenDTOList = new ArrayList<>(); // Khởi tạo list
        db.collection("quyen") // Lấy từ Firestore
                .orderBy("tenQuyen") // Sắp xếp (tùy chọn)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> dataAdapter = new ArrayList<>();
                        quyenDTOList.clear(); // Xóa list cũ
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuyenDTO quyen = document.toObject(QuyenDTO.class);
                            quyen.setMaQuyen(Integer.parseInt(document.getId())); // Lấy ID document làm mã quyền

                            quyenDTOList.add(quyen);
                            dataAdapter.add(quyen.getTenQuyen());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dataAdapter);
                        spinQuyen.setAdapter(adapter);

                        // Kiểm tra và khóa spinner nếu là admin đầu tiên
                        if (laQuanLyDauTien) {
                            spinQuyen.setSelection(dataAdapter.indexOf("Quản lý")); // Tự động chọn "Quản lý"
                            spinQuyen.setEnabled(false);
                        }

                    } else {
                        Log.w(TAG, "Lỗi lấy danh sách quyền.", task.getException());
                        Toast.makeText(this, "Lỗi tải danh sách quyền", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void dongY() {
        String tenDN = edTenDangNhap.getText().toString().trim();
        String matKhau = edMatKhau.getText().toString().trim();
        String cmnd = edCMND.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(tenDN) || TextUtils.isEmpty(matKhau)) {
            Toast.makeText(this, "Tên đăng nhập và mật khẩu không được trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgGioiTinh.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu
        NhanVienDTO nhanVienDTO = new NhanVienDTO();
        nhanVienDTO.setTENDANGNHAP(tenDN);
        nhanVienDTO.setMATKHAU(matKhau);
        nhanVienDTO.setCMND(cmnd); // Luôn dùng String cho CMND/CCCD
        nhanVienDTO.setNGAYSINH(edNgaySinh.getText().toString());
        nhanVienDTO.setGIOITINH((rgGioiTinh.getCheckedRadioButtonId() == R.id.rdNam) ? "Nam" : "Nữ");

        int vitri = spinQuyen.getSelectedItemPosition();
        nhanVienDTO.setMAQUYEN(quyenDTOList.get(vitri).getMaQuyen());
        // SỬA LỖI: Gán quyền chính xác cho trường hợp Quản lý đầu tiên
        if (laQuanLyDauTien) {
            nhanVienDTO.setMAQUYEN(Contants.QUYEN_QUANLY); // Gán cứng quyền Quản lý
        } else {
            if (quyenDTOList != null && !quyenDTOList.isEmpty()){
                nhanVienDTO.setMAQUYEN(quyenDTOList.get(vitri).getMaQuyen());
            }
        }


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
            dongY();
        } else if (id == R.id.btnThoatDK) {
            finish();
        } else if (id == R.id.edNgaySinhDK) {
            chooseDay();
        }
    }


}
