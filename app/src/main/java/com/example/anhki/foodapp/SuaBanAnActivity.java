package com.example.anhki.foodapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch; // Import WriteBatch

public class SuaBanAnActivity extends AppCompatActivity {
    private static final String TAG = "SuaBanAnActivity";

    private EditText edtSuaTenBan;
    private Button btnDongYSuaBan;

    // Firebase
    private FirebaseFirestore db;
    private String banAnDocId; // ID của document cần sửa

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suabanan); // Đảm bảo dùng đúng layout

        edtSuaTenBan = findViewById(R.id.edSuaTenBanAn); // Đảm bảo đúng ID
        btnDongYSuaBan = findViewById(R.id.btnDongYSuaBanAn); // Đảm bảo đúng ID

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy Document ID từ Intent
        banAnDocId = getIntent().getStringExtra("banAnDocId");
        if (banAnDocId == null || banAnDocId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy bàn ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCurrentData(); // Tải tên bàn hiện tại

        btnDongYSuaBan.setOnClickListener(v -> luuThayDoiVaoFirestore());
    }

    private void loadCurrentData() {
        DocumentReference docRef = db.collection("banAn").document(banAnDocId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String tenBanHienTai = documentSnapshot.getString("tenBan");
                edtSuaTenBan.setText(tenBanHienTai);
            } else {
                Log.w(TAG, "Không tìm thấy document với ID: " + banAnDocId);
                Toast.makeText(this, "Không tìm thấy thông tin bàn", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi khi tải tên bàn hiện tại", e);
            Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void luuThayDoiVaoFirestore() {
        String tenBanMoi = edtSuaTenBan.getText().toString().trim();

        // 1. Validate
        if (TextUtils.isEmpty(tenBanMoi)) {
            Toast.makeText(this, "Tên bàn không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra trùng tên (Nâng cao) - Thực hiện truy vấn trước khi cập nhật
        db.collection("banAn")
                .whereEqualTo("tenBan", tenBanMoi)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Kiểm tra xem có document nào trùng tên và *khác* document hiện tại không
                        boolean trungTen = false;
                        if (!task.getResult().isEmpty()) {
                            // Chỉ coi là trùng nếu document tìm thấy không phải là chính nó
                            if (!task.getResult().getDocuments().get(0).getId().equals(banAnDocId)) {
                                trungTen = true;
                            }
                        }

                        if (trungTen) {
                            Toast.makeText(SuaBanAnActivity.this, "Tên bàn này đã tồn tại!", Toast.LENGTH_SHORT).show();
                        } else {
                            // 3. Tiến hành cập nhật
                            capNhatTenBan(tenBanMoi);
                        }
                    } else {
                        Log.w(TAG, "Lỗi kiểm tra trùng tên bàn: ", task.getException());
                        Toast.makeText(SuaBanAnActivity.this, "Lỗi kiểm tra dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void capNhatTenBan(String tenBanMoi) {
        DocumentReference banRef = db.collection("banAn").document(banAnDocId);

        // Chỉ cập nhật trường "tenBan"
        banRef.update("tenBan", tenBanMoi)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cập nhật tên bàn thành công!");
                    Toast.makeText(SuaBanAnActivity.this, getString(R.string.suathanhcong), Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK); // Báo thành công về Fragment
                    finish(); // Đóng Activity
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi khi cập nhật tên bàn", e);
                    Toast.makeText(SuaBanAnActivity.this, getString(R.string.loi), Toast.LENGTH_SHORT).show();
                });
    }
}