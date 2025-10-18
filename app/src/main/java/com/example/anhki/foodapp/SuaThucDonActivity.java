package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anhki.foodapp.DAO.MonAnDAO;
import com.example.anhki.foodapp.DTO.MonAnDTO;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SuaThucDonActivity extends AppCompatActivity {

    private EditText edTenMon, edGiaTien;
    private ImageView imHinhMonAn;
    private Button btnDongYSua;
    private MonAnDAO monAnDAO;
    private int mamon;
    private MonAnDTO monAnDTO; // Biến để lưu thông tin món ăn hiện tại
    private byte[] hinhAnhBytes; // Biến để lưu dữ liệu ảnh

    // Launcher để chọn ảnh
    private final ActivityResultLauncher<Intent> moHinhLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
//                            // Chuyển ảnh mới chọn thành byte[] và hiển thị
//                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            imHinhMonAn.setImageBitmap(bitmap);
//
//                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                            hinhAnhBytes = stream.toByteArray();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                            // 1. Đọc ảnh gốc từ Uri
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

                            // 2. THU NHỎ ẢNH GỐC
                            Bitmap resizedBitmap = resizeBitmap(originalBitmap, 800); // Đặt chiều lớn nhất là 800px

                            // 3. Hiển thị ảnh đã thu nhỏ để xem trước
                            imHinhMonAn.setImageBitmap(resizedBitmap);

                            // 4. Chuyển ảnh ĐÃ THU NHỎ thành byte[] để lưu
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream); // Giảm chất lượng xuống 80 để file nhẹ hơn
                            hinhAnhBytes = stream.toByteArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    public Bitmap resizeBitmap(Bitmap originalImage, int maxSize) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) { // Ảnh rộng hơn cao
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else { // Ảnh cao hơn rộng hoặc ảnh vuông
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(originalImage, width, height, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suathucdon);

        // Ánh xạ
        edTenMon = findViewById(R.id.edSuaTenMon);
        edGiaTien = findViewById(R.id.edSuaGiaTien);
        imHinhMonAn = findViewById(R.id.imSuaHinhMonAn);
        btnDongYSua = findViewById(R.id.btnDongYSuaMon);
        monAnDAO = new MonAnDAO(this);

        // Lấy mã món ăn và tải dữ liệu
        mamon = getIntent().getIntExtra("mamon", 0);
        loadData();

        // Gán sự kiện
        imHinhMonAn.setOnClickListener(v -> moThuVienAnh());
        btnDongYSua.setOnClickListener(v -> luuThayDoi());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (monAnDAO != null) {
            monAnDAO.close();
        }
    }

    private void loadData() {
        if (mamon != 0) {
            monAnDTO = monAnDAO.LayMonAnTheoId(mamon);
            if (monAnDTO != null) {
                edTenMon.setText(monAnDTO.getTenMonAn());
                edGiaTien.setText(String.valueOf(monAnDTO.getGiaTien())); // Chuyển int thành String

                hinhAnhBytes = monAnDTO.getHinhAnh(); // Lưu lại ảnh cũ
                if (hinhAnhBytes != null && hinhAnhBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnhBytes, 0, hinhAnhBytes.length);
                    imHinhMonAn.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void moThuVienAnh() {
        Intent iMoHinh = new Intent(Intent.ACTION_PICK);
        iMoHinh.setType("image/*");
        moHinhLauncher.launch(iMoHinh);
    }

    private void luuThayDoi() {
        String tenMon = edTenMon.getText().toString().trim();
        String giaTienStr = edGiaTien.getText().toString().trim();

        if (TextUtils.isEmpty(tenMon) || TextUtils.isEmpty(giaTienStr)) {
            Toast.makeText(this, "Vui lòng nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int giaTien = Integer.parseInt(giaTienStr);

            // Cập nhật thông tin vào đối tượng DTO
            monAnDTO.setTenMonAn(tenMon);
            monAnDTO.setGiaTien(giaTien);
            monAnDTO.setHinhAnh(hinhAnhBytes); // Gán ảnh (có thể là ảnh cũ hoặc ảnh mới)

            // Gọi DAO để cập nhật
            boolean kiemtra = monAnDAO.CapNhatMonAn(monAnDTO);
            if (kiemtra) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá tiền phải là một con số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}