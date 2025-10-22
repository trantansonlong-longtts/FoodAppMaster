
package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // Import TextUtils
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.firestore.FirebaseFirestore;

// Bỏ import DAO
// import com.example.anhki.foodapp.DAO.BanAnDAO;

import java.util.HashMap;
import java.util.Map;

public class ThemBanAnActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ThemBanAnActivity"; // Thêm TAG

    private EditText edTenThemBanAn;
    private Button btnDongYThemBanAn, btnThoatThemBanAn; // Thêm nút Thoát nếu có

    // Firebase
    private FirebaseFirestore db;

    // Bỏ BanAnDAO
    // private BanAnDAO banAnDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_thembanan);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        edTenThemBanAn = findViewById(R.id.edTenThemBanAn);
        btnDongYThemBanAn = findViewById(R.id.btnDongYThemBanAn);
        // btnThoatThemBanAn = findViewById(R.id.btnThoatThemBanAn); // Ánh xạ nếu có

        // Bỏ khởi tạo BanAnDAO
        // banAnDAO = new BanAnDAO(this);

        // Gán sự kiện
        btnDongYThemBanAn.setOnClickListener(this);
        // if (btnThoatThemBanAn != null) btnThoatThemBanAn.setOnClickListener(this);
    }

    // Bỏ onDestroy vì không còn DAO
    // @Override
    // protected void onDestroy() { ... }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnDongYThemBanAn) {
            themBanAnVaoFirestore(); // Gọi hàm thêm mới
        }
        // else if (id == R.id.btnThoatThemBanAn) {
        //     finish();
        // }
    }

    private void themBanAnVaoFirestore() {
        String tenBanAn = edTenThemBanAn.getText().toString().trim();

        // 1. Validate dữ liệu (giữ nguyên)
        if (TextUtils.isEmpty(tenBanAn)) {
            Toast.makeText(this, "Vui lòng nhập tên bàn ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. KIỂM TRA TÊN BÀN TỒN TẠI TRÊN FIRESTORE
        db.collection("banAn")
                .whereEqualTo("tenBan", tenBanAn) // Tìm document có trường "tenBan" trùng khớp
                .limit(1) // Chỉ cần tìm 1 document là đủ để biết đã tồn tại
                .get() // Thực hiện truy vấn lấy dữ liệu một lần
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Kiểm tra kết quả truy vấn
                        if (task.getResult().isEmpty()) {
                            // KHÔNG TÌM THẤY BÀN NÀO TRÙNG TÊN -> TIẾN HÀNH THÊM MỚI
                            Log.d(TAG, "Tên bàn '" + tenBanAn + "' chưa tồn tại, tiến hành thêm.");
                            taoBanAnMoi(tenBanAn);
                        } else {
                            // TÌM THẤY BÀN TRÙNG TÊN -> BÁO LỖI
                            Log.w(TAG, "Tên bàn '" + tenBanAn + "' đã tồn tại.");
                            Toast.makeText(ThemBanAnActivity.this, "Tên bàn này đã tồn tại, vui lòng chọn tên khác", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Lỗi khi thực hiện truy vấn kiểm tra
                        Log.w(TAG, "Lỗi kiểm tra tên bàn: ", task.getException());
                        Toast.makeText(ThemBanAnActivity.this, "Lỗi khi kiểm tra tên bàn", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Tách hàm tạo bàn mới ra riêng cho rõ ràng
    private void taoBanAnMoi(String tenBanAn) {
        // Tạo đối tượng Map chứa dữ liệu bàn mới
        Map<String, Object> banData = new HashMap<>();
        banData.put("tenBan", tenBanAn);
        banData.put("tinhTrang", "false"); // Bàn mới luôn trống

        // Thêm document mới vào collection "banAn"
        db.collection("banAn")
                .add(banData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Thêm bàn thành công với ID: " + documentReference.getId());
                    Toast.makeText(ThemBanAnActivity.this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish(); // Đóng Activity
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi khi thêm bàn", e);
                    Toast.makeText(ThemBanAnActivity.this, getString(R.string.themthatbai), Toast.LENGTH_SHORT).show();
                });
    }
}
