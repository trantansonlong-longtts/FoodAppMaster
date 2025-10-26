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
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anhki.foodapp.DAO.NhanVienDAO;
import com.example.anhki.foodapp.DAO.QuyenDAO;
import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.DTO.QuyenDTO;

import java.util.ArrayList;
import java.util.List;

public class DangKyActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edTenDangNhap, edMatKhau, edNgaySinh, edCMND;
    private Button btnDongY, btnThoat;
    private TextView txtTieuDeDangKy;
    private RadioGroup rgGioiTinh;
    private Spinner spinQuyen;

    private int manhanvien = 0; // =0 là thêm mới, !=0 là cập nhật

    private NhanVienDAO nhanVienDAO;
    private QuyenDAO quyenDAO;
    private List<QuyenDTO> quyenDTOList;
    private boolean laQuanLyDauTien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangky);

        // Khởi tạo DAO
        nhanVienDAO = new NhanVienDAO(this);
        quyenDAO = new QuyenDAO(this);

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
            hienThiThongTinNhanVien();
        }

        laQuanLyDauTien = getIntent().getBooleanExtra("laQuanLyDauTien", false);
        if (laQuanLyDauTien) {
            // Nếu là Quản lý đầu tiên, ẩn Spinner đi và mặc định quyền là Quản lý
            spinQuyen.setVisibility(View.GONE);
            // Trong hàm luuNhanVien(), bạn sẽ cần gán cứng MAQUYEN = Constants.QUYEN_QUANLY

            // Hoặc đơn giản hơn là khóa Spinner lại
            // spinQuyen.setSelection(0); // Giả sử Quản lý ở vị trí 0
            // spinQuyen.setEnabled(false);
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

    private void hienThiThongTinNhanVien() {
        NhanVienDTO nhanVienDTO = nhanVienDAO.LayDanhSachNhanVienTheoMa(manhanvien);

        // CẢI TIẾN: Kiểm tra null để tránh crash
        if (nhanVienDTO == null) {
            Toast.makeText(this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        edTenDangNhap.setText(nhanVienDTO.getTENDANGNHAP());
        edMatKhau.setText(nhanVienDTO.getMATKHAU());
        edCMND.setText(nhanVienDTO.getCMND()); // Giả sử CMND là String
        edNgaySinh.setText(nhanVienDTO.getNGAYSINH());

        if ("Nam".equals(nhanVienDTO.getGIOITINH())) {
            rgGioiTinh.check(R.id.rdNam);
        } else {
            rgGioiTinh.check(R.id.rdNu);
        }

        // CẢI TIẾN: Sửa lỗi logic quan trọng - Hiển thị đúng quyền của nhân viên trên Spinner
        int maQuyenCuaNhanVien = nhanVienDTO.getMAQUYEN();
        for (int i = 0; i < quyenDTOList.size(); i++) {
            if (quyenDTOList.get(i).getMaQuyen() == maQuyenCuaNhanVien) {
                spinQuyen.setSelection(i);
                break;
            }
        }
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

        // Thực hiện thêm hoặc sửa
        boolean kiemtra;
        if (manhanvien != 0) {
            nhanVienDTO.setMANV(manhanvien);
            kiemtra = nhanVienDAO.SuaNhanVien(nhanVienDTO);
            Toast.makeText(this, kiemtra ? "Cập nhật thành công" : "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        } else {
            kiemtra = nhanVienDAO.ThemNhanVien(nhanVienDTO);
            Toast.makeText(this, kiemtra ? "Thêm thành công" : "Thêm thất bại", Toast.LENGTH_SHORT).show();
        }

        if (kiemtra) {
            setResult(Activity.RESULT_OK); // Đặt kết quả để màn hình trước có thể nhận biết
            finish();
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

    // CẢI TIẾN: Đóng kết nối DAO để chống rò rỉ bộ nhớ
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nhanVienDAO != null) {
            nhanVienDAO.close();
        }
        if (quyenDAO != null) {
            quyenDAO.close();
        }
    }
}
