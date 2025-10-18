
package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // Sử dụng TextUtils để kiểm tra chuỗi rỗng
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anhki.foodapp.DAO.BanAnDAO;

public class ThemBanAnActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edTenThemBanAn;
    private BanAnDAO banAnDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_thembanan);

        // Khởi tạo DAO trong onCreate
        banAnDAO = new BanAnDAO(this);

        // Ánh xạ View
        edTenThemBanAn = findViewById(R.id.edTenThemBanAn);
        Button btnDongYThemBanAn = findViewById(R.id.btnDongYThemBanAn);

        // Thiết lập listener
        btnDongYThemBanAn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Gọi hàm xử lý logic thêm bàn
        xuLyThemBanAn();
    }

    private void xuLyThemBanAn() {
        String tenBanAn = edTenThemBanAn.getText().toString().trim();

        // 1. Validate dữ liệu đầu vào
        if (TextUtils.isEmpty(tenBanAn)) {
            Toast.makeText(this, "Vui lòng nhập tên bàn ăn", Toast.LENGTH_SHORT).show();
            return; // Dừng xử lý
        }

        // 2. Kiểm tra tên bàn đã tồn tại chưa
        if (banAnDAO.KiemTraTenBanAnTonTai(tenBanAn)) {
            Toast.makeText(this, "Tên bàn này đã tồn tại, vui lòng chọn tên khác", Toast.LENGTH_SHORT).show();
            return; // Dừng xử lý
        }

        // 3. Thực hiện thêm bàn và trả kết quả
        boolean themThanhCong = banAnDAO.ThemBanAn(tenBanAn);

        Intent intent = new Intent();
        intent.putExtra("ketquathem", themThanhCong);
        setResult(Activity.RESULT_OK, intent);
        finish(); // Đóng Activity sau khi hoàn tất
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Rất quan trọng: Đóng kết nối database để tránh rò rỉ bộ nhớ
        if (banAnDAO != null) {
            banAnDAO.close();
        }
    }
}
