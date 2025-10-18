package com.example.anhki.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anhki.foodapp.DAO.LoaiMonAnDAO;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ThemLoaiThucDonActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnDongYThemLoaiThucDon, btnThoatThemLoaiThucDon;
    private EditText edTenLoai;
    private ImageView imHinhLoaiThucDon;
    private LoaiMonAnDAO loaiMonAnDAO;

    private byte[] hinhAnhBytes = null; // Biến để lưu dữ liệu ảnh dạng byte[]

    // Cải tiến: Dùng ActivityResultLauncher thay cho onActivityResult
    private final ActivityResultLauncher<Intent> chonHinhLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            // 1. Đọc ảnh gốc từ Uri
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

                            // 2. THU NHỎ ẢNH GỐC
                            Bitmap resizedBitmap = resizeBitmap(originalBitmap, 800); // Đặt chiều lớn nhất là 800px

                            // 3. Hiển thị ảnh đã thu nhỏ để xem trước
                            imHinhLoaiThucDon.setImageBitmap(resizedBitmap);

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
            }
    );
    // Hàm này nhận vào một Bitmap gốc và trả về một Bitmap mới đã được thu nhỏ
    // mà vẫn giữ đúng tỷ lệ
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
        setContentView(R.layout.layout_themloaithucdon);

        loaiMonAnDAO = new LoaiMonAnDAO(this);

        btnDongYThemLoaiThucDon = findViewById(R.id.btnDongYThemLoaiThucDon);
        edTenLoai = findViewById(R.id.edThemLoaiThucDon);
        imHinhLoaiThucDon = findViewById(R.id.imHinhLoaiThucDon);
        btnThoatThemLoaiThucDon = findViewById(R.id.btnThoatThemLoaiThucDon);

        btnDongYThemLoaiThucDon.setOnClickListener(this);
        btnThoatThemLoaiThucDon.setOnClickListener(this);
        imHinhLoaiThucDon.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loaiMonAnDAO != null) {
            loaiMonAnDAO.close();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnDongYThemLoaiThucDon) {
            themLoaiThucDon();
        } else if (id == R.id.imHinhLoaiThucDon) {
            Intent iMoHinh = new Intent(Intent.ACTION_PICK);
            iMoHinh.setType("image/*");
            chonHinhLauncher.launch(Intent.createChooser(iMoHinh, "Chọn hình loại thực đơn"));
        } else if (id == R.id.btnThoatThemLoaiThucDon) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }



    private void themLoaiThucDon() {
        String sTenLoaiThucDon = edTenLoai.getText().toString().trim();

        if (sTenLoaiThucDon.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.vuilongnhapdulieu), Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra đã chọn ảnh chưa (dựa vào biến byte[])
        if (hinhAnhBytes == null) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        if (loaiMonAnDAO.KiemTraTrungTenLoai(sTenLoaiThucDon)) {
            Toast.makeText(this, "Tên loại thực đơn đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm vào database với dữ liệu ảnh là byte[]
        boolean kiemtra = loaiMonAnDAO.ThemLoaiMonAn(sTenLoaiThucDon, hinhAnhBytes);
        Intent iDuLieu = new Intent();
        iDuLieu.putExtra("kiemtraloaithucdon", kiemtra);
        setResult(Activity.RESULT_OK, iDuLieu);
        finish();
    }

}
