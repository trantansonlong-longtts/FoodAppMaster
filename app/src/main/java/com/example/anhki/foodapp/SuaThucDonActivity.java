//package com.example.anhki.foodapp;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
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
//import com.example.anhki.foodapp.DAO.MonAnDAO;
//import com.example.anhki.foodapp.DTO.MonAnDTO;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//
//public class SuaThucDonActivity extends AppCompatActivity {
//
//    private EditText edTenMon, edGiaTien;
//    private ImageView imHinhMonAn;
//    private Button btnDongYSua;
//    private MonAnDAO monAnDAO;
//    private int mamon;
//    private MonAnDTO monAnDTO; // Biến để lưu thông tin món ăn hiện tại
//    private byte[] hinhAnhBytes; // Biến để lưu dữ liệu ảnh
//
//    // Launcher để chọn ảnh
//    private final ActivityResultLauncher<Intent> moHinhLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    Uri imageUri = result.getData().getData();
//                    if (imageUri != null) {
//                        try {
////                            // Chuyển ảnh mới chọn thành byte[] và hiển thị
////                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
////                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////                            imHinhMonAn.setImageBitmap(bitmap);
////
////                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
////                            hinhAnhBytes = stream.toByteArray();
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                        }
//                            // 1. Đọc ảnh gốc từ Uri
//                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
//
//                            // 2. THU NHỎ ẢNH GỐC
//                            Bitmap resizedBitmap = resizeBitmap(originalBitmap, 800); // Đặt chiều lớn nhất là 800px
//
//                            // 3. Hiển thị ảnh đã thu nhỏ để xem trước
//                            imHinhMonAn.setImageBitmap(resizedBitmap);
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
//            });
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
//        setContentView(R.layout.layout_suathucdon);
//
//        // Ánh xạ
//        edTenMon = findViewById(R.id.edSuaTenMon);
//        edGiaTien = findViewById(R.id.edSuaGiaTien);
//        imHinhMonAn = findViewById(R.id.imSuaHinhMonAn);
//        btnDongYSua = findViewById(R.id.btnDongYSuaMon);
//        monAnDAO = new MonAnDAO(this);
//
//        // Lấy mã món ăn và tải dữ liệu
//        mamon = getIntent().getIntExtra("mamon", 0);
//        loadData();
//
//        // Gán sự kiện
//        imHinhMonAn.setOnClickListener(v -> moThuVienAnh());
//        btnDongYSua.setOnClickListener(v -> luuThayDoi());
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (monAnDAO != null) {
//            monAnDAO.close();
//        }
//    }
//
//    private void loadData() {
//        if (mamon != 0) {
//            monAnDTO = monAnDAO.LayMonAnTheoId(mamon);
//            if (monAnDTO != null) {
//                edTenMon.setText(monAnDTO.getTenMonAn());
//                edGiaTien.setText(String.valueOf(monAnDTO.getGiaTien())); // Chuyển int thành String
//
//                hinhAnhBytes = monAnDTO.getHinhAnh(); // Lưu lại ảnh cũ
//                if (hinhAnhBytes != null && hinhAnhBytes.length > 0) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnhBytes, 0, hinhAnhBytes.length);
//                    imHinhMonAn.setImageBitmap(bitmap);
//                }
//            }
//        }
//    }
//
//    private void moThuVienAnh() {
//        Intent iMoHinh = new Intent(Intent.ACTION_PICK);
//        iMoHinh.setType("image/*");
//        moHinhLauncher.launch(iMoHinh);
//    }
//
//    private void luuThayDoi() {
//        String tenMon = edTenMon.getText().toString().trim();
//        String giaTienStr = edGiaTien.getText().toString().trim();
//
//        if (TextUtils.isEmpty(tenMon) || TextUtils.isEmpty(giaTienStr)) {
//            Toast.makeText(this, "Vui lòng nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            int giaTien = Integer.parseInt(giaTienStr);
//
//            // Cập nhật thông tin vào đối tượng DTO
//            monAnDTO.setTenMonAn(tenMon);
//            monAnDTO.setGiaTien(giaTien);
//            monAnDTO.setHinhAnh(hinhAnhBytes); // Gán ảnh (có thể là ảnh cũ hoặc ảnh mới)
//
//            // Gọi DAO để cập nhật
//            boolean kiemtra = monAnDAO.CapNhatMonAn(monAnDTO);
//            if (kiemtra) {
//                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
//                setResult(Activity.RESULT_OK);
//                finish();
//            } else {
//                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Giá tiền phải là một con số hợp lệ", Toast.LENGTH_SHORT).show();
//        }
//    }
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
import android.widget.TextView; // Import TextView
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
// Bỏ DAO
// import com.example.anhki.foodapp.DAO.MonAnDAO;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SuaThucDonActivity extends AppCompatActivity {

    private static final String TAG = "SuaThucDonActivity";

    private EditText edTenMon, edGiaTien;
    private ImageView imHinhMonAn;
    private Button btnDongYSua, btnThoatSua; // Thêm nút Thoát nếu có
    private TextView txtLoaiMonAnDisplay; // TextView hiển thị tên loại (không cho sửa)

    // Firebase
    private FirebaseFirestore db;
    private String monAnDocId; // ID document món ăn cần sửa
    private DocumentReference currentLoaiRef; // Reference đến loại món ăn hiện tại

    private byte[] hinhAnhBytes = null; // Dữ liệu ảnh dạng byte[]

    // Launcher chọn ảnh
    private final ActivityResultLauncher<Intent> moHinhLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                            Bitmap resizedBitmap = resizeBitmap(originalBitmap, 500); // Resize ảnh
                            imHinhMonAn.setImageBitmap(resizedBitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 80, stream); // Dùng WEBP
                            hinhAnhBytes = stream.toByteArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suathucdon); // Đảm bảo đúng layout

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        edTenMon = findViewById(R.id.edSuaTenMon);
        edGiaTien = findViewById(R.id.edSuaGiaTien);
        imHinhMonAn = findViewById(R.id.imSuaHinhMonAn);
        btnDongYSua = findViewById(R.id.btnDongYSuaMon);
        // txtLoaiMonAnDisplay = findViewById(R.id.txtLoaiMonAnDisplay); // Ánh xạ TextView tên loại nếu có
        // btnThoatSua = findViewById(R.id.btnThoatSuaMon); // Ánh xạ nếu có

        // Lấy Document ID món ăn từ Intent
        monAnDocId = getIntent().getStringExtra("mamon"); // Giả sử Fragment vẫn gửi key "mamon"
        if (monAnDocId == null || monAnDocId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMonAnData(); // Tải dữ liệu hiện tại

        // Gán sự kiện
        imHinhMonAn.setOnClickListener(v -> moThuVienAnh());
        btnDongYSua.setOnClickListener(v -> luuThayDoiVaoFirestore());
        // if (btnThoatSua != null) btnThoatSua.setOnClickListener(v -> finish());
    }

    // Không cần onDestroy vì không còn DAO

    private void loadMonAnData() {
        DocumentReference docRef = db.collection("monAn").document(monAnDocId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    edTenMon.setText(document.getString("tenMonAn"));
                    // Lấy giá tiền kiểu Number và chuyển sang String
                    Long giaTienLong = document.getLong("giaTien");
                    edGiaTien.setText(giaTienLong != null ? String.valueOf(giaTienLong) : "0");

                    // Lưu lại Reference loại hiện tại
                    currentLoaiRef = document.getDocumentReference("maLoaiRef");

                    // Tải và hiển thị tên loại (nếu có TextView)
                    // loadTenLoai();

                    // Lấy ảnh Base64 và hiển thị
                    String hinhAnhBase64 = document.getString("hinhAnh");
                    if (hinhAnhBase64 != null && !hinhAnhBase64.isEmpty()) {
                        try {
                            hinhAnhBytes = Base64.decode(hinhAnhBase64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnhBytes, 0, hinhAnhBytes.length);
                            imHinhMonAn.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi giải mã Base64", e);
                            imHinhMonAn.setImageResource(R.drawable.logodangnhap);
                        }
                    } else {
                        imHinhMonAn.setImageResource(R.drawable.logodangnhap);
                    }
                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(this, "Không tìm thấy dữ liệu món ăn", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /* // Hàm tải tên loại (nếu cần)
    private void loadTenLoai() {
        if (currentLoaiRef != null) {
            currentLoaiRef.get().addOnSuccessListener(doc -> {
                if (doc.exists() && txtLoaiMonAnDisplay != null) {
                    txtLoaiMonAnDisplay.setText("Loại: " + doc.getString("tenLoai"));
                }
            });
        }
    } */

    private void moThuVienAnh() {
        Intent iMoHinh = new Intent(Intent.ACTION_PICK);
        iMoHinh.setType("image/*");
        moHinhLauncher.launch(iMoHinh);
    }

    private void luuThayDoiVaoFirestore() {
        String tenMonMoi = edTenMon.getText().toString().trim();
        String giaTienStr = edGiaTien.getText().toString().trim();

        if (TextUtils.isEmpty(tenMonMoi) || TextUtils.isEmpty(giaTienStr)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        // Ảnh không bắt buộc phải chọn lại khi sửa
        // if (hinhAnhBytes == null) { ... }

        try {
            int giaTienMoi = Integer.parseInt(giaTienStr);
            String hinhAnhBase64 = "";
            if (hinhAnhBytes != null) {
                hinhAnhBase64 = Base64.encodeToString(hinhAnhBytes, Base64.DEFAULT);
            }

            // Tạo Map chứa các trường cần cập nhật
            Map<String, Object> updates = new HashMap<>();
            updates.put("tenMonAn", tenMonMoi);
            updates.put("giaTien", giaTienMoi);
            updates.put("hinhAnh", hinhAnhBase64);
            // Không cập nhật maLoaiRef ở đây (nếu muốn đổi loại, cần Spinner và logic khác)

            // Thực hiện cập nhật
            db.collection("monAn").document(monAnDocId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Cập nhật món ăn thành công!");
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Lỗi cập nhật món ăn", e);
                        Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá tiền phải là một con số hợp lệ", Toast.LENGTH_SHORT).show();
        }
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