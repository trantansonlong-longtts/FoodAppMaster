//gemini
package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.anhki.foodapp.DAO.QuyenDAO;
import com.example.anhki.foodapp.DAO.NhanVienDAO;

public class DangNhapActivity extends AppCompatActivity implements View.OnClickListener {
    // TỐI ƯU: Dùng hằng số để quản lý SharedPreferences, tránh lỗi chính tả
    private static final String PREFS_NAME = "luuquyen";
    private static final String KEY_MAQUYEN = "maquyen";

    private EditText edTenDangNhap, edMatKhau;
    private NhanVienDAO nhanVienDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangnhap);

        edTenDangNhap = findViewById(R.id.edTenDangNhapDN);
        edMatKhau = findViewById(R.id.edMatKhauDN);
        Button btnDongY = findViewById(R.id.btnDongYDN);
        //Button btnDangKy = findViewById(R.id.btnDongYDN2);

        nhanVienDAO = new NhanVienDAO(this);


        btnDongY.setOnClickListener(this);
        //btnDangKy.setOnClickListener(this);


        // DI CHUYỂN LOGIC CÀI ĐẶT LẦN ĐẦU VỀ ĐÂY
        caiDatLanDau();

        // KIỂM TRA QUYỀN QUẢN LÝ
        kiemTraQuanLy();

        // TỐI ƯU: Tự động đăng ký nhân viên đầu tiên nếu chưa có ai
        //kiemTraVaTaoNhanVienDauTien();
    }
    private void caiDatLanDau() {
        SharedPreferences sharedPreferences = getSharedPreferences("SPR_MOLANDAU", 0);
        boolean firstOpen = sharedPreferences.getBoolean("MOLANDAU", true);
        if (firstOpen) {
            QuyenDAO quyenDAO = new QuyenDAO(this);
            quyenDAO.ThemQuyen("Quản lý"); // ID = 1
            quyenDAO.ThemQuyen("Nhân viên"); // ID = 2
            quyenDAO.close();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("MOLANDAU", false);
            editor.apply();
        }
    }

    private void kiemTraQuanLy() {
        // SỬA LỖI: Kiểm tra xem đã có tài khoản Quản lý nào chưa
        if (!nhanVienDAO.KiemTraQuanLyTonTai()) {
            Intent iDangKy = new Intent(this, DangKyActivity.class);
            iDangKy.putExtra("laQuanLyDauTien", true); // Gửi cờ để báo hiệu
            startActivity(iDangKy);
            Toast.makeText(this, "Vui lòng đăng ký tài khoản Quản lý đầu tiên!", Toast.LENGTH_LONG).show();
        }
    }

    private void kiemTraVaTaoNhanVienDauTien() {
        // Kiểm tra xem đã có nhân viên nào trong CSDL chưa
        if (!nhanVienDAO.KiemTraNhanVien()) {
            // Nếu chưa có, chuyển đến màn hình đăng ký để tạo nhân viên đầu tiên (mặc định là admin)
            Intent iDangKy = new Intent(DangNhapActivity.this, DangKyActivity.class);
            startActivity(iDangKy);
            Toast.makeText(this, "Chưa có nhân viên, vui lòng đăng ký tài khoản quản lý đầu tiên!", Toast.LENGTH_LONG).show();
        }
    }

    private void xuLyDangNhap() {
        String sTenDangNhap = edTenDangNhap.getText().toString();
        String sMatKhau = edMatKhau.getText().toString();

        if (TextUtils.isEmpty(sTenDangNhap) || TextUtils.isEmpty(sMatKhau)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int manhanvien = nhanVienDAO.KiemTraDangNhap(sTenDangNhap, sMatKhau);
        if (manhanvien > 0) { // Đăng nhập thành công nếu mã nhân viên > 0
            // Lấy mã quyền của nhân viên đó
            int maquyen = nhanVienDAO.LayQuyenNhanVien(manhanvien);

            // Lưu mã quyền vào SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_MAQUYEN, maquyen);
            editor.apply();

            // Chuyển sang trang chủ
            Intent iTrangChu = new Intent(DangNhapActivity.this, TrangChuActicity.class);
            iTrangChu.putExtra("tendn", sTenDangNhap);
            iTrangChu.putExtra("manhanvien", manhanvien);
            startActivity(iTrangChu);
            overridePendingTransition(R.anim.hieuung_activity_vao, R.anim.hieuung_activity_ra);
            finish(); // Đóng màn hình đăng nhập
        } else {
            Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnDongYDN) {
            xuLyDangNhap();
        }
//        } else if (id == R.id.btnDongYDN2) {
//            Intent iDangKy = new Intent(DangNhapActivity.this, DangKyActivity.class);
//            startActivity(iDangKy);
//        }
    }

    // TỐI ƯU: Luôn đóng kết nối CSDL khi Activity bị hủy
    @Override

    protected void onDestroy() {
        super.onDestroy();
        if (nhanVienDAO != null) {
            nhanVienDAO.close();
        }
    }
}