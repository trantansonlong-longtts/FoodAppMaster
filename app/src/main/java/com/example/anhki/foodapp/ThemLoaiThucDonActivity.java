//package com.example.anhki.foodapp;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.anhki.foodapp.DAO.LoaiMonAnDAO;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//
//public class ThemLoaiThucDonActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private Button btnDongYThemLoaiThucDon, btnThoatThemLoaiThucDon;
//    private EditText edTenLoai;
//    private ImageView imHinhLoaiThucDon;
//    private LoaiMonAnDAO loaiMonAnDAO;
//
//    private byte[] hinhAnhBytes = null; // Biến để lưu dữ liệu ảnh dạng byte[]
//
//    // Cải tiến: Dùng ActivityResultLauncher thay cho onActivityResult
//    private final ActivityResultLauncher<Intent> chonHinhLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    Uri imageUri = result.getData().getData();
//                    if (imageUri != null) {
//                        try {
//                            // 1. Đọc ảnh gốc từ Uri
//                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
//
//                            // 2. THU NHỎ ẢNH GỐC
//                            Bitmap resizedBitmap = resizeBitmap(originalBitmap, 800); // Đặt chiều lớn nhất là 800px
//
//                            // 3. Hiển thị ảnh đã thu nhỏ để xem trước
//                            imHinhLoaiThucDon.setImageBitmap(resizedBitmap);
//
//                            // 4. Chuyển ảnh ĐÃ THU NHỎ thành byte[] để lưu
//                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream); // Giảm chất lượng xuống 80 để file nhẹ hơn
//                            hinhAnhBytes = stream.toByteArray();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//    );
//    // Hàm này nhận vào một Bitmap gốc và trả về một Bitmap mới đã được thu nhỏ
//    // mà vẫn giữ đúng tỷ lệ
//    public Bitmap resizeBitmap(Bitmap originalImage, int maxSize) {
//        int width = originalImage.getWidth();
//        int height = originalImage.getHeight();
//
//        float bitmapRatio = (float) width / (float) height;
//        if (bitmapRatio > 1) { // Ảnh rộng hơn cao
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else { // Ảnh cao hơn rộng hoặc ảnh vuông
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(originalImage, width, height, true);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_themloaithucdon);
//
//        loaiMonAnDAO = new LoaiMonAnDAO(this);
//
//        btnDongYThemLoaiThucDon = findViewById(R.id.btnDongYThemLoaiThucDon);
//        edTenLoai = findViewById(R.id.edThemLoaiThucDon);
//        imHinhLoaiThucDon = findViewById(R.id.imHinhLoaiThucDon);
//        btnThoatThemLoaiThucDon = findViewById(R.id.btnThoatThemLoaiThucDon);
//
//        btnDongYThemLoaiThucDon.setOnClickListener(this);
//        btnThoatThemLoaiThucDon.setOnClickListener(this);
//        imHinhLoaiThucDon.setOnClickListener(this);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (loaiMonAnDAO != null) {
//            loaiMonAnDAO.close();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btnDongYThemLoaiThucDon) {
//            themLoaiThucDon();
//        } else if (id == R.id.imHinhLoaiThucDon) {
//            Intent iMoHinh = new Intent(Intent.ACTION_PICK);
//            iMoHinh.setType("image/*");
//            chonHinhLauncher.launch(Intent.createChooser(iMoHinh, "Chọn hình loại thực đơn"));
//        } else if (id == R.id.btnThoatThemLoaiThucDon) {
//            setResult(Activity.RESULT_CANCELED);
//            finish();
//        }
//    }
//
//
//
//    private void themLoaiThucDon() {
//        String sTenLoaiThucDon = edTenLoai.getText().toString().trim();
//
//        if (sTenLoaiThucDon.isEmpty()) {
//            Toast.makeText(this, getResources().getString(R.string.vuilongnhapdulieu), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Kiểm tra đã chọn ảnh chưa (dựa vào biến byte[])
//        if (hinhAnhBytes == null) {
//            Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (loaiMonAnDAO.KiemTraTrungTenLoai(sTenLoaiThucDon)) {
//            Toast.makeText(this, "Tên loại thực đơn đã tồn tại", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Thêm vào database với dữ liệu ảnh là byte[]
//        boolean kiemtra = loaiMonAnDAO.ThemLoaiMonAn(sTenLoaiThucDon, hinhAnhBytes);
//        Intent iDuLieu = new Intent();
//        iDuLieu.putExtra("kiemtraloaithucdon", kiemtra);
//        setResult(Activity.RESULT_OK, iDuLieu);
//        finish();
//    }
//
//}
package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64; // Import Base64
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.firestore.FirebaseFirestore;

// Bỏ DAO
// import com.example.anhki.foodapp.DAO.LoaiMonAnDAO;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ThemLoaiThucDonActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ThemLoaiTDActivity";

    private Button btnDongYThemLoaiThucDon, btnThoatThemLoaiThucDon;
    private EditText edTenLoai;
    private ImageView imHinhLoaiThucDon;

    // Firebase
    private FirebaseFirestore db;

    private byte[] hinhAnhBytes = null; // Vẫn dùng byte[] để xử lý ảnh

    // Launcher chọn ảnh (giữ nguyên)
    private final ActivityResultLauncher<Intent> chonHinhLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // ... (Logic xử lý ảnh sang byte[] giữ nguyên)
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                            Bitmap resizedBitmap = resizeBitmap(originalBitmap, 500); // Resize ảnh
                            imHinhLoaiThucDon.setImageBitmap(resizedBitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 80, stream); // Dùng WEBP, chất lượng 80
                            hinhAnhBytes = stream.toByteArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Không thể tải hình ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_themloaithucdon);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        btnDongYThemLoaiThucDon = findViewById(R.id.btnDongYThemLoaiThucDon);
        edTenLoai = findViewById(R.id.edThemLoaiThucDon);
        imHinhLoaiThucDon = findViewById(R.id.imHinhLoaiThucDon);
        btnThoatThemLoaiThucDon = findViewById(R.id.btnThoatThemLoaiThucDon);

        // Gán sự kiện
        btnDongYThemLoaiThucDon.setOnClickListener(this);
        btnThoatThemLoaiThucDon.setOnClickListener(this);
        imHinhLoaiThucDon.setOnClickListener(this);
    }

    // Không cần onDestroy vì không còn DAO

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnDongYThemLoaiThucDon) {
            themLoaiVaoFirestore();
        } else if (id == R.id.imHinhLoaiThucDon) {
            Intent iMoHinh = new Intent(Intent.ACTION_PICK);
            iMoHinh.setType("image/*");
            chonHinhLauncher.launch(Intent.createChooser(iMoHinh, "Chọn hình loại thực đơn"));
        } else if (id == R.id.btnThoatThemLoaiThucDon) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    private void themLoaiVaoFirestore() {
        String tenLoai = edTenLoai.getText().toString().trim();

        if (TextUtils.isEmpty(tenLoai)) {
            Toast.makeText(this, getResources().getString(R.string.vuilongnhapdulieu), Toast.LENGTH_SHORT).show();
            return;
        }
        if (hinhAnhBytes == null) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // KIỂM TRA TRÙNG TÊN TRƯỚC KHI THÊM
        db.collection("loaiMonAn")
                .whereEqualTo("tenLoai", tenLoai)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Không trùng -> Tiến hành thêm
                            taoLoaiMonAnMoi(tenLoai);
                        } else {
                            Toast.makeText(this, "Tên loại thực đơn đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Lỗi kiểm tra trùng tên loại: ", task.getException());
                        Toast.makeText(this, "Lỗi kiểm tra dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void taoLoaiMonAnMoi(String tenLoai) {
        // Chuyển byte[] thành String Base64 để lưu vào Firestore
        String hinhAnhBase64 = Base64.encodeToString(hinhAnhBytes, Base64.DEFAULT);

        Map<String, Object> loaiData = new HashMap<>();
        loaiData.put("tenLoai", tenLoai);
        loaiData.put("hinhAnh", hinhAnhBase64); // Lưu ảnh dạng Base64 String

        db.collection("loaiMonAn")
                .add(loaiData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Thêm loại thành công với ID: " + documentReference.getId());
                    Toast.makeText(this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi khi thêm loại", e);
                    Toast.makeText(this, getString(R.string.themthatbai), Toast.LENGTH_SHORT).show();
                });
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