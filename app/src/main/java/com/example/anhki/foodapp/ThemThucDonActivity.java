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
//import android.widget.TextView;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.anhki.foodapp.DAO.MonAnDAO;
//import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;
//import com.example.anhki.foodapp.DAO.LoaiMonAnDAO;
//import com.example.anhki.foodapp.DTO.MonAnDTO;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//
//public class ThemThucDonActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private ImageView imHinhThucDon;
//    private Button btnDongYThemMonAn, btnThoatThemMonAn;
//    private EditText edTenMonAn, edGiaTien;
//    private MonAnDAO monAnDAO;
//    private LoaiMonAnDAO loaiMonAnDAO; // Thêm biến này
//
//    private TextView txtTenLoaiContext; // Thêm biến này
//
//    private byte[] hinhAnhBytes = null;
//    private int maloai; // Biến để lưu mã loại được truyền vào
//
//    private final ActivityResultLauncher<Intent> moHinhLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    Uri imageUri = result.getData().getData();
//                    if (imageUri != null) {
//                        try {
////                        {
////                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
////                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////                            imHinhThucDon.setImageBitmap(bitmap);
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
//                            imHinhThucDon.setImageBitmap(resizedBitmap);
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
//        setContentView(R.layout.layout_themthucdon);
//
//        monAnDAO = new MonAnDAO(this);
//        loaiMonAnDAO = new LoaiMonAnDAO(this);//khởi tạo loaiMonAnDAO
//
//        // Nhận mã loại từ Fragment gửi qua
//        maloai = getIntent().getIntExtra("maloai", -1);
//        imHinhThucDon = findViewById(R.id.imHinhThucDon);
//        txtTenLoaiContext = findViewById(R.id.txtTenLoaiContext); // Ánh xạ TextView mới
//        // Lấy và hiển thị tên loại
//        if (maloai != -1) {
//            String tenloai = loaiMonAnDAO.LayTenLoaiTheoMa(maloai);
//            txtTenLoaiContext.setText("Thêm vào loại: " + tenloai);
//        } else {
//            // Xử lý lỗi
//            Toast.makeText(this, "Lỗi: không tìm thấy loại món ăn", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Ánh xạ View
//        imHinhThucDon = findViewById(R.id.imHinhThucDon);
//        btnDongYThemMonAn = findViewById(R.id.btnDongYThemMonAn);
//        btnThoatThemMonAn = findViewById(R.id.btnThoatThemMonAn);
//        edTenMonAn = findViewById(R.id.edThemTenMonAn);
//        edGiaTien = findViewById(R.id.edThemGiaTien);
//
//        // Gán sự kiện
//        imHinhThucDon.setOnClickListener(this);
//        btnDongYThemMonAn.setOnClickListener(this);
//        btnThoatThemMonAn.setOnClickListener(this);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (monAnDAO != null) monAnDAO.close();
//        if (loaiMonAnDAO != null) loaiMonAnDAO.close();
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.imHinhThucDon) {
//            Intent iMoHinh = new Intent(Intent.ACTION_PICK);
//            iMoHinh.setType("image/*");
//            moHinhLauncher.launch(Intent.createChooser(iMoHinh, "Chọn hình thực đơn"));
//        } else if (id == R.id.btnDongYThemMonAn) {
//            themMonAn();
//        } else if (id == R.id.btnThoatThemMonAn) {
//            finish();
//        }
//    }
//
//
//    private void themMonAn() {
//        String tenmonan = edTenMonAn.getText().toString().trim();
//        String giatienStr = edGiaTien.getText().toString().trim();
//
//        if (TextUtils.isEmpty(tenmonan) || TextUtils.isEmpty(giatienStr)) {
//            Toast.makeText(this, getString(R.string.loithemmonan), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (hinhAnhBytes == null) {
//            Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            int giatien = Integer.parseInt(giatienStr);
//
//            MonAnDTO monAnDTO = new MonAnDTO();
//            monAnDTO.setTenMonAn(tenmonan);
//            monAnDTO.setGiaTien(giatien);
//            monAnDTO.setMaLoai(maloai); // Lấy mã loại đã được truyền vào
//            monAnDTO.setHinhAnh(hinhAnhBytes);
//
//            boolean kiemtra = monAnDAO.ThemMonAn(monAnDTO);
//            if (kiemtra) {
//                Toast.makeText(this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
//                setResult(Activity.RESULT_OK);
//                finish();
//            } else {
//                Toast.makeText(this, getString(R.string.themthatbai), Toast.LENGTH_SHORT).show();
//            }
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Giá tiền phải là một con số!", Toast.LENGTH_SHORT).show();
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
import com.google.firebase.firestore.FirebaseFirestore;

// Bỏ DAO không cần thiết
// import com.example.anhki.foodapp.DAO.MonAnDAO;
// import com.example.anhki.foodapp.DAO.LoaiMonAnDAO; // Chỉ cần nếu muốn load tên loại

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ThemThucDonActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ThemThucDonActivity"; // Thêm TAG

    private ImageView imHinhThucDon;
    private Button btnDongYThemMonAn, btnThoatThemMonAn;
    private EditText edTenMonAn, edGiaTien;
    private TextView txtTenLoaiContext; // TextView để hiển thị tên loại

    // Firebase
    private FirebaseFirestore db;

    private byte[] hinhAnhBytes = null;
    private String loaiMonAnDocId; // Lưu Document ID của loại món ăn

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
                            imHinhThucDon.setImageBitmap(resizedBitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 80, stream); // Dùng WEBP
                            hinhAnhBytes = stream.toByteArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_themthucdon); // Đảm bảo layout đã bỏ Spinner

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Nhận Document ID của loại món ăn từ Fragment
        loaiMonAnDocId = getIntent().getStringExtra("loaiMonAnDocId");
        if (loaiMonAnDocId == null || loaiMonAnDocId.isEmpty()) {
            Toast.makeText(this, "Lỗi: không tìm thấy loại món ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ View
        imHinhThucDon = findViewById(R.id.imHinhThucDon);
        btnDongYThemMonAn = findViewById(R.id.btnDongYThemMonAn);
        btnThoatThemMonAn = findViewById(R.id.btnThoatThemMonAn);
        edTenMonAn = findViewById(R.id.edThemTenMonAn);
        edGiaTien = findViewById(R.id.edThemGiaTien);
        txtTenLoaiContext = findViewById(R.id.txtTenLoaiContext); // Ánh xạ TextView tên loại

        // Tải và hiển thị tên loại món ăn
        loadTenLoai();

        // Gán sự kiện
        imHinhThucDon.setOnClickListener(this);
        btnDongYThemMonAn.setOnClickListener(this);
        btnThoatThemMonAn.setOnClickListener(this);
    }

    // Không cần onDestroy vì không còn DAO

    // Hàm tải tên loại từ Firestore
    private void loadTenLoai() {
        DocumentReference loaiRef = db.collection("loaiMonAn").document(loaiMonAnDocId);
        loaiRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String tenLoai = documentSnapshot.getString("tenLoai");
                if (txtTenLoaiContext != null) { // Kiểm tra null phòng trường hợp layout chưa có
                    txtTenLoaiContext.setText("Thêm vào loại: " + tenLoai);
                }
            }
        }).addOnFailureListener(e -> Log.w(TAG,"Lỗi tải tên loại: ", e));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imHinhThucDon) {
            Intent iMoHinh = new Intent(Intent.ACTION_PICK);
            iMoHinh.setType("image/*");
            moHinhLauncher.launch(Intent.createChooser(iMoHinh, "Chọn hình thực đơn"));
        } else if (id == R.id.btnDongYThemMonAn) {
            themMonAnVaoFirestore(); // Gọi hàm lưu lên Firestore
        } else if (id == R.id.btnThoatThemMonAn) {
            finish();
        }
    }

    private void themMonAnVaoFirestore() {
        String tenmonan = edTenMonAn.getText().toString().trim();
        String giatienStr = edGiaTien.getText().toString().trim();

        if (TextUtils.isEmpty(tenmonan) || TextUtils.isEmpty(giatienStr)) {
            Toast.makeText(this, getString(R.string.loithemmonan), Toast.LENGTH_SHORT).show();
            return;
        }
        if (hinhAnhBytes == null) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int giatien = Integer.parseInt(giatienStr);

            // 1. Tạo Document Reference cho loại món ăn
            DocumentReference loaiRef = db.collection("loaiMonAn").document(loaiMonAnDocId);

            // 2. Chuyển hình ảnh sang Base64 String
            String hinhAnhBase64 = Base64.encodeToString(hinhAnhBytes, Base64.DEFAULT);

            // 3. Tạo Map dữ liệu để lưu
            Map<String, Object> monAnData = new HashMap<>();
            monAnData.put("tenMonAn", tenmonan);
            monAnData.put("giaTien", giatien);
            monAnData.put("maLoaiRef", loaiRef); // Lưu Reference
            monAnData.put("hinhAnh", hinhAnhBase64); // Lưu ảnh Base64

            // 4. Thêm document mới vào collection "monAn"
            db.collection("monAn")
                    .add(monAnData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Thêm món ăn thành công với ID: " + documentReference.getId());
                        Toast.makeText(this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Lỗi khi thêm món ăn", e);
                        Toast.makeText(this, getString(R.string.themthatbai), Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá tiền phải là một con số!", Toast.LENGTH_SHORT).show();
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