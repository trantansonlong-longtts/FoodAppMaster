//SuaLoaiThucDonActivity
package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anhki.foodapp.DAO.LoaiMonAnDAO;
import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SuaLoaiThucDonActivity extends AppCompatActivity {

    private EditText edtSuaTenLoai;
    private ImageView imSuaHinhLoai;
    private Button btnLuu;
    private LoaiMonAnDAO loaiMonAnDAO;
    private int maloai;
    private byte[] hinhAnhBytes; // Biến để lưu dữ liệu ảnh

    // Dùng ActivityResultLauncher để chọn ảnh
    private final ActivityResultLauncher<Intent> chonHinhLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
//                            // Chuyển ảnh mới chọn thành byte[] và hiển thị
//                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            imSuaHinhLoai.setImageBitmap(bitmap);
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
                            imSuaHinhLoai.setImageBitmap(resizedBitmap);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sualoaithucdon);

        edtSuaTenLoai = findViewById(R.id.edtSuaTenLoai);
        imSuaHinhLoai = findViewById(R.id.imSuaHinhLoai);
        btnLuu = findViewById(R.id.btnLuuSuaLoai);
        loaiMonAnDAO = new LoaiMonAnDAO(this);

        maloai = getIntent().getIntExtra("maloai", -1);

        // Tải thông tin cũ của loại món ăn
        loadData();

        // Sự kiện click
        imSuaHinhLoai.setOnClickListener(v -> moThuVienAnh());
        btnLuu.setOnClickListener(v -> luuThayDoi());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loaiMonAnDAO != null) {
            loaiMonAnDAO.close();
        }
    }

    private void loadData(){
        if(maloai != -1) {
            LoaiMonAnDTO loaiMonAnDTO = loaiMonAnDAO.LayLoaiMonAnTheoMa(maloai);
            if (loaiMonAnDTO != null) {
                edtSuaTenLoai.setText(loaiMonAnDTO.getTenLoai());
                hinhAnhBytes = loaiMonAnDTO.getHinhAnh(); // Lưu lại ảnh cũ

                // Hiển thị ảnh cũ
                if (hinhAnhBytes != null && hinhAnhBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnhBytes, 0, hinhAnhBytes.length);
                    imSuaHinhLoai.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void moThuVienAnh(){
        Intent iMoHinh = new Intent(Intent.ACTION_PICK);
        iMoHinh.setType("image/*");
        chonHinhLauncher.launch(iMoHinh);
    }

    private void luuThayDoi() {
        String tenMoi = edtSuaTenLoai.getText().toString().trim();
        if (tenMoi.isEmpty()) {
            Toast.makeText(this, "Tên loại không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        LoaiMonAnDTO loaiMonAnDTO = new LoaiMonAnDTO();
        loaiMonAnDTO.setMaLoai(maloai);
        loaiMonAnDTO.setTenLoai(tenMoi);
        loaiMonAnDTO.setHinhAnh(hinhAnhBytes); // Gán ảnh (có thể là ảnh cũ hoặc ảnh mới)

        boolean kq = loaiMonAnDAO.CapNhatLoaiMonAn(loaiMonAnDTO);
        if (kq) {
            Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Sửa thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
