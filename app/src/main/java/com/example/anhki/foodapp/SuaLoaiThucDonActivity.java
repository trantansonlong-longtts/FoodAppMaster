package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SuaLoaiThucDonActivity extends AppCompatActivity {
    private static final String TAG = "SuaLoaiTDActivity";

    private EditText edtSuaTenLoai;
    private ImageView imSuaHinhLoai;
    private Button btnLuu;

    // Firebase
    private FirebaseFirestore db;
    private String loaiMonAnDocId;
    private String currentTenLoai = ""; // Lưu tên loại hiện tại

    private byte[] hinhAnhBytes = null; // Dữ liệu ảnh hiện tại hoặc mới

    // Launcher chọn ảnh (giữ nguyên)
    private final ActivityResultLauncher<Intent> chonHinhLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // ... (Logic xử lý ảnh sang byte[] giữ nguyên như ở ThemLoaiThucDonActivity)
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                            Bitmap resizedBitmap = resizeBitmap(originalBitmap, 500);
                            imSuaHinhLoai.setImageBitmap(resizedBitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 80, stream);
                            hinhAnhBytes = stream.toByteArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sualoaithucdon);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        edtSuaTenLoai = findViewById(R.id.edtSuaTenLoai);
        imSuaHinhLoai = findViewById(R.id.imSuaHinhLoai);
        btnLuu = findViewById(R.id.btnLuuSuaLoai);

        // Lấy Document ID từ Intent
        loaiMonAnDocId = getIntent().getStringExtra("loaiMonAnDocId");
        if (loaiMonAnDocId == null || loaiMonAnDocId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy loại món ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCurrentData(); // Tải dữ liệu hiện tại từ Firestore

        // Gán sự kiện
        imSuaHinhLoai.setOnClickListener(v -> moThuVienAnh());
        btnLuu.setOnClickListener(v -> luuThayDoiVaoFirestore());
    }


    private void loadCurrentData() {
        DocumentReference docRef = db.collection("loaiMonAn").document(loaiMonAnDocId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentTenLoai = documentSnapshot.getString("tenLoai");
                edtSuaTenLoai.setText(currentTenLoai);

                // Lấy ảnh dạng Base64 String và chuyển về byte[]
                String hinhAnhBase64 = documentSnapshot.getString("hinhAnh");
                if (hinhAnhBase64 != null && !hinhAnhBase64.isEmpty()) {
                    try {
                        hinhAnhBytes = Base64.decode(hinhAnhBase64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnhBytes, 0, hinhAnhBytes.length);
                        imSuaHinhLoai.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi giải mã ảnh Base64", e);
                        imSuaHinhLoai.setImageResource(R.drawable.logodangnhap); // Ảnh lỗi
                    }
                } else {
                    imSuaHinhLoai.setImageResource(R.drawable.logodangnhap); // Ảnh mặc định
                }
            } else {
                Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void moThuVienAnh() {
        Intent iMoHinh = new Intent(Intent.ACTION_PICK);
        iMoHinh.setType("image/*");
        chonHinhLauncher.launch(iMoHinh);
    }

    private void luuThayDoiVaoFirestore() {
        String tenMoi = edtSuaTenLoai.getText().toString().trim();
        if (TextUtils.isEmpty(tenMoi)) {
            Toast.makeText(this, "Tên loại không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu tên không đổi thì không cần kiểm tra trùng
        if (tenMoi.equals(currentTenLoai)) {
            capNhatLoaiMonAn(tenMoi);
        } else {
            // KIỂM TRA TRÙNG TÊN MỚI
            db.collection("loaiMonAn")
                    .whereEqualTo("tenLoai", tenMoi)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // Không trùng -> Tiến hành cập nhật
                                capNhatLoaiMonAn(tenMoi);
                            } else {
                                Toast.makeText(this, "Tên loại này đã tồn tại!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Lỗi kiểm tra dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void capNhatLoaiMonAn(String tenMoi) {
        String hinhAnhBase64 = "";
        if (hinhAnhBytes != null) {
            hinhAnhBase64 = Base64.encodeToString(hinhAnhBytes, Base64.DEFAULT);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("tenLoai", tenMoi);
        updates.put("hinhAnh", hinhAnhBase64);

        db.collection("loaiMonAn").document(loaiMonAnDocId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show());
    }

    // Hàm resize ảnh (giữ nguyên)
    public Bitmap resizeBitmap(Bitmap originalImage, int maxSize) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) { width = maxSize; height = (int) (width / bitmapRatio); }
        else { height = maxSize; width = (int) (height * bitmapRatio); }
        return Bitmap.createScaledBitmap(originalImage, width, height, true);
    }
}
