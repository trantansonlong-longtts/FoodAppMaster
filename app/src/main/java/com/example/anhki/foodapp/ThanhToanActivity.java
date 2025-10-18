package com.example.anhki.foodapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiThanhToan;
import com.example.anhki.foodapp.DAO.BanAnDAO;
import com.example.anhki.foodapp.DAO.GoiMonDAO;
import com.example.anhki.foodapp.DTO.ThanhToanDTO;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ThanhToanActivity extends AppCompatActivity implements View.OnClickListener {
    private GridView gridView;
    private Button btnThanhToan, btnThoat;
    private TextView txtTongTien;
    private GoiMonDAO goiMonDAO;
    private BanAnDAO banAnDAO;
    private List<ThanhToanDTO> thanhToanDTOList;
    private AdapterHienThiThanhToan adapterHienThiThanhToan;

    private int maban;
    private int magoimon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_thanhtoan);

        // Ánh xạ View
        gridView = findViewById(R.id.gvThanhToan);
        btnThanhToan = findViewById(R.id.btnThanhToan);
        btnThoat = findViewById(R.id.btnThoatThanhToan);
        txtTongTien = findViewById(R.id.txtTongTien);

        // Khởi tạo DAO
        goiMonDAO = new GoiMonDAO(this);
        banAnDAO = new BanAnDAO(this);

        // Lấy mã bàn từ Intent
        maban = getIntent().getIntExtra("maban", 0);

        if (maban != 0) {
            // Lấy mã gọi món dựa vào mã bàn
            magoimon = (int) goiMonDAO.LayMaGoiMonTheoMaBan(maban, "false");
            // Hiển thị danh sách món và tính tổng tiền
            hienThiDanhSachVaTinhTongTien();
        }

        btnThanhToan.setOnClickListener(this);
        btnThoat.setOnClickListener(this);
    }

    private void hienThiDanhSachVaTinhTongTien() {
        thanhToanDTOList = goiMonDAO.LayDanhSachMonAnTheoMaGoiMon(magoimon);

        adapterHienThiThanhToan = new AdapterHienThiThanhToan(this, R.layout.custom_layout_hienthithanhtoan, thanhToanDTOList);
        gridView.setAdapter(adapterHienThiThanhToan);

        // Tính tổng tiền
        long tongTien = 0;
        for (ThanhToanDTO thanhToanDTO : thanhToanDTOList) {
            tongTien += (long) thanhToanDTO.getSoLuong() * thanhToanDTO.getGiatien();
        }

        // ĐỊNH DẠNG TỔNG TIỀN VÀ HIỂN THỊ
        String tongTienFormatted = formatVND(tongTien);
        txtTongTien.setText(getString(R.string.tongcong) + " " + tongTienFormatted);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnThanhToan) {
            boolean kiemtrabanan = banAnDAO.CapNhatTinhTrangBan(maban, "false");
            boolean kiemtragoimon = goiMonDAO.CapNhatTrangThaiGoiMonTheoMaBan(maban, "true");

            if (kiemtrabanan && kiemtragoimon) {
                Toast.makeText(this, getString(R.string.thanhtoanthanhcong), Toast.LENGTH_SHORT).show();
                hienThiDanhSachVaTinhTongTien(); // Tải lại danh sách (sẽ trống) và reset tổng tiền về 0
            } else {
                Toast.makeText(this, getString(R.string.loi), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btnThoatThanhToan) {
            finish();
        }
    }

    // TỐI ƯU: Thêm hàm onDestroy để đóng kết nối CSDL, chống rò rỉ bộ nhớ
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (goiMonDAO != null) {
            goiMonDAO.close();
        }
        if (banAnDAO != null) {
            banAnDAO.close();
        }
    }

    // HÀM ĐỊNH DẠNG TIỀN TỆ
    private String formatVND(long amount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");
        return formatter.format(amount) + " VNĐ";
    }
}