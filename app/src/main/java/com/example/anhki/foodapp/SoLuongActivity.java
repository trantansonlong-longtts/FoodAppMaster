
package com.example.anhki.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anhki.foodapp.DAO.GoiMonDAO;
import com.example.anhki.foodapp.DTO.ChiTietGoiMonDTO;

public class SoLuongActivity extends AppCompatActivity implements View.OnClickListener {
    int maban, mamonan;
    Button btnDongY, btnTang, btnGiam;
    EditText edSoLuong;
    GoiMonDAO goiMonDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_themsoluong);

        // Ánh xạ view
        btnDongY = findViewById(R.id.btnDongYThemSoLuong);
        btnTang = findViewById(R.id.btnTangSoLuong);
        btnGiam = findViewById(R.id.btnGiamSoLuong);
        edSoLuong = findViewById(R.id.edSoLuongMonAn);

        // Mặc định số lượng = 0
        edSoLuong.setText("0");

        goiMonDAO = new GoiMonDAO(this);

        // Lấy dữ liệu truyền vào
        Intent intent = getIntent();
        maban = intent.getIntExtra("maban", 0);
        mamonan = intent.getIntExtra("mamon", 0);

        // Sự kiện click
        btnDongY.setOnClickListener(this);
        btnTang.setOnClickListener(v -> tangSoLuong());
        btnGiam.setOnClickListener(v -> giamSoLuong());
    }

    private void tangSoLuong() {
        int sl = Integer.parseInt(edSoLuong.getText().toString());
        sl++;
        edSoLuong.setText(String.valueOf(sl));
    }

    private void giamSoLuong() {
        int sl = Integer.parseInt(edSoLuong.getText().toString());
        if (sl > 0) sl--;
        edSoLuong.setText(String.valueOf(sl));
    }

    @Override
    public void onClick(View v) {
        int soluong = Integer.parseInt(edSoLuong.getText().toString());

        if (soluong > 0) {
            int magoimon = (int) goiMonDAO.LayMaGoiMonTheoMaBan(maban, "false");
            boolean kiemtra = goiMonDAO.KiemTraMonAnDaTonTai(magoimon, mamonan);

            if (kiemtra) {
                // cập nhật số lượng món đã tồn tại
                int soluongcu = goiMonDAO.LaySoLuongMonAnTheoMaGoiMon(magoimon, mamonan);
                int tongsoluong = soluongcu + soluong;

                ChiTietGoiMonDTO chiTiet = new ChiTietGoiMonDTO();
                chiTiet.setMaGoiMon(magoimon);
                chiTiet.setMaMonAn(mamonan);
                chiTiet.setSoLuong(tongsoluong);

                boolean ok = goiMonDAO.CapNhatSoLuong(chiTiet);
                if (ok)
                    Toast.makeText(this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, getString(R.string.themthatbai), Toast.LENGTH_SHORT).show();

            } else {
                // thêm món mới
                ChiTietGoiMonDTO chiTiet = new ChiTietGoiMonDTO();
                chiTiet.setMaGoiMon(magoimon);
                chiTiet.setMaMonAn(mamonan);
                chiTiet.setSoLuong(soluong);

                boolean ok = goiMonDAO.ThemChiTietGoiMon(chiTiet);
                if (ok)
                    Toast.makeText(this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, getString(R.string.themthatbai), Toast.LENGTH_SHORT).show();
            }
        }

        // Trả kết quả về Fragment
        setResult(RESULT_OK);
        finish();
    }
}

// giề ấy
//kaka
