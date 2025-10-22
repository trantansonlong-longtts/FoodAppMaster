//gemini
package com.example.anhki.foodapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog; // Thêm import cho AlertDialog
import androidx.appcompat.app.AppCompatActivity;
import com.example.anhki.foodapp.DAO.QuyenDAO;
//import com.example.anhki.foodapp.DAO.NhanVienDAO;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DangNhapActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DangNhapActivity";
    // TỐI ƯU: Dùng hằng số để quản lý SharedPreferences, tránh lỗi chính tả
    private static final String PREFS_NAME = "luuquyen";
    private static final String KEY_MAQUYEN = "maquyen";

    private EditText edTenDangNhap, edMatKhau;
    private Button btnDongY, btnDangKy;
    private TextView txtQuenMatKhau; // Thêm TextView
    //private NhanVienDAO nhanVienDAO;
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangnhap);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        edTenDangNhap = findViewById(R.id.edTenDangNhapDN);
        edMatKhau = findViewById(R.id.edMatKhauDN);
        btnDongY = findViewById(R.id.btnDongYDN);
        btnDangKy = findViewById(R.id.btnDongYDN2);
        txtQuenMatKhau = findViewById(R.id.txtQuenMatKhau); // Ánh xạ TextView

        //nhanVienDAO = new NhanVienDAO(this);


        btnDongY.setOnClickListener(this);
        btnDangKy.setOnClickListener(this);
        txtQuenMatKhau.setOnClickListener(this); // Gán sự kiện


        // DI CHUYỂN LOGIC CÀI ĐẶT LẦN ĐẦU VỀ ĐÂY
        caiDatLanDau();

        // KIỂM TRA QUYỀN QUẢN LÝ chuyển sang DangkyActivity
        //kiemTraQuanLy();

        // TỐI ƯU: Tự động đăng ký nhân viên đầu tiên nếu chưa có ai
        //kiemTraVaTaoNhanVienDauTien();
    }
    private void caiDatLanDau() {
        SharedPreferences sharedPreferences = getSharedPreferences("SPR_MOLANDAU", 0);
        boolean firstOpen = sharedPreferences.getBoolean("MOLANDAU", true);
        if (firstOpen) {
            QuyenDAO quyenDAO = new QuyenDAO(this);
            // Kiểm tra xem quyền đã tồn tại chưa trước khi thêm
            if (quyenDAO.LayDanhSachQuyen().isEmpty()){
                quyenDAO.ThemQuyen("Quản lý"); // ID = 1
                quyenDAO.ThemQuyen("Nhân viên"); // ID = 2
            }
            quyenDAO.close();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("MOLANDAU", false);
            editor.apply();
        }
    }

//    private void kiemTraQuanLy() {
//        // SỬA LỖI: Kiểm tra xem đã có tài khoản Quản lý nào chưa
//        if (!nhanVienDAO.KiemTraQuanLyTonTai()) {
//            Intent iDangKy = new Intent(this, DangKyActivity.class);
//            iDangKy.putExtra("laQuanLyDauTien", true); // Gửi cờ để báo hiệu
//            startActivity(iDangKy);
//            Toast.makeText(this, "Vui lòng đăng ký tài khoản Quản lý đầu tiên!", Toast.LENGTH_LONG).show();
//        }
//    }

//    private void kiemTraVaTaoNhanVienDauTien() {
//        // Kiểm tra xem đã có nhân viên nào trong CSDL chưa
//        if (!nhanVienDAO.KiemTraNhanVien()) {
//            // Nếu chưa có, chuyển đến màn hình đăng ký để tạo nhân viên đầu tiên (mặc định là admin)
//            Intent iDangKy = new Intent(DangNhapActivity.this, DangKyActivity.class);
//            startActivity(iDangKy);
//            Toast.makeText(this, "Chưa có nhân viên, vui lòng đăng ký tài khoản quản lý đầu tiên!", Toast.LENGTH_LONG).show();
//        }
//    }

    // Hàm xử lý đăng nhập bằng Firebase
    private void xuLyDangNhap() {
        String email = edTenDangNhap.getText().toString().trim();
        String password = edMatKhau.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập Auth thành công
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Lấy UID và truy vấn Firestore để lấy mã quyền
                            layMaQuyenVaChuyenTrang(user.getUid(), email);
                        }
                    } else {
                        // Đăng nhập Auth thất bại
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(DangNhapActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        // Ẩn loading
                    }
                });


//        int manhanvien = nhanVienDAO.KiemTraDangNhap(sTenDangNhap, sMatKhau);
//        if (manhanvien > 0) { // Đăng nhập thành công nếu mã nhân viên > 0
//            // Lấy mã quyền của nhân viên đó
//            int maquyen = nhanVienDAO.LayQuyenNhanVien(manhanvien);
//
//            // Lưu mã quyền vào SharedPreferences
//            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt(KEY_MAQUYEN, maquyen);
//            editor.apply();
//
//            // Chuyển sang trang chủ
//            Intent iTrangChu = new Intent(DangNhapActivity.this, TrangChuActicity.class);
//            iTrangChu.putExtra("tendn", sTenDangNhap);
//            iTrangChu.putExtra("manhanvien", manhanvien);
//            startActivity(iTrangChu);
//            overridePendingTransition(R.anim.hieuung_activity_vao, R.anim.hieuung_activity_ra);
//            finish(); // Đóng màn hình đăng nhập
//        } else {
//            Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
//        }
    }
    private void layMaQuyenVaChuyenTrang(String uid, String email) {
        DocumentReference docRef = db.collection("nhanVien").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Lấy mã quyền từ Firestore (giả sử lưu là Long/Number)
                    Long maQuyenLong = document.getLong("maQuyen");
                    int maquyen = (maQuyenLong != null) ? maQuyenLong.intValue() : -1; // -1 nếu không có quyền

                    if (maquyen != -1) {
                        // Lưu mã quyền vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(KEY_MAQUYEN, maquyen);
                        editor.apply();

                        // Chuyển sang trang chủ
                        Intent iTrangChu = new Intent(DangNhapActivity.this, TrangChuActicity.class);
                        iTrangChu.putExtra("tendn", email); // Gửi email thay vì tên đăng nhập cũ
                        iTrangChu.putExtra("uid", uid); // Có thể gửi UID nếu cần
                        startActivity(iTrangChu);
                        overridePendingTransition(R.anim.hieuung_activity_vao, R.anim.hieuung_activity_ra);
                        finish(); // Đóng màn hình đăng nhập
                    } else {
                        Log.w(TAG, "Không tìm thấy mã quyền trong Firestore cho user: " + uid);
                        Toast.makeText(DangNhapActivity.this, "Lỗi: Không tìm thấy thông tin quyền.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Không tìm thấy document trong Firestore cho user: " + uid);
                    Toast.makeText(DangNhapActivity.this, "Lỗi: Không tìm thấy thông tin nhân viên.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "Lỗi khi lấy document Firestore: ", task.getException());
                Toast.makeText(DangNhapActivity.this, "Lỗi kết nối.", Toast.LENGTH_SHORT).show();
            }
            // Ẩn loading (nếu có)
        });
    }
    // Hàm xử lý quên mật khẩu (dùng Firebase Auth)
    private void xuLyQuenMatKhauFirebase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đặt lại mật khẩu");
        builder.setMessage("Nhập email đăng nhập của bạn:");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            String emailAddress = input.getText().toString().trim();
            if (!TextUtils.isEmpty(emailAddress)) {
                mAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email đặt lại mật khẩu đã gửi.");
                                Toast.makeText(DangNhapActivity.this, "Email đặt lại mật khẩu đã được gửi.", Toast.LENGTH_LONG).show();
                            } else {
                                Log.w(TAG, "Lỗi gửi email đặt lại mật khẩu", task.getException());
                                Toast.makeText(DangNhapActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnDongYDN) {
            xuLyDangNhap(); // Gọi hàm đăng nhập Firebase mới
        } else if (id == R.id.btnDongYDN2) {
            Intent iDangKy = new Intent(DangNhapActivity.this, DangKyActivity.class);
            // Kiểm tra quản lý tồn tại trước khi cho phép đăng ký mới
            kiemTraQuanLyTruocKhiDangKy();
            // startActivity(iDangKy); // Chuyển việc mở sang hàm kiemTraQuanLyTruocKhiDangKy
        } else if (id == R.id.txtQuenMatKhau) {
            xuLyQuenMatKhauFirebase(); // Gọi hàm quên mật khẩu Firebase mới
        }
//        int id = view.getId();
//        if (id == R.id.btnDongYDN) {
//            xuLyDangNhap();
//        }
//        } else if (id == R.id.btnDongYDN2) {
//            Intent iDangKy = new Intent(DangNhapActivity.this, DangKyActivity.class);
//            startActivity(iDangKy);
//        }
    }
    // Hàm kiểm tra Quản lý trước khi cho đăng ký (để tránh tạo nhiều QL)
    private void kiemTraQuanLyTruocKhiDangKy() {
        // Chúng ta cần đọc từ Firestore xem có Quản lý nào chưa
        db.collection("nhanVien")
                .whereEqualTo("maQuyen", Contants.QUYEN_QUANLY)
                .limit(1) // Chỉ cần tìm 1 là đủ
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // CHƯA CÓ QUẢN LÝ -> Mở đăng ký và báo là đăng ký QL đầu tiên
                            Intent iDangKy = new Intent(this, DangKyActivity.class);
                            iDangKy.putExtra("laQuanLyDauTien", true);
                            startActivity(iDangKy);
                        } else {
                            // ĐÃ CÓ QUẢN LÝ -> Mở đăng ký bình thường (cho phép tạo Nhân viên)
                            Intent iDangKy = new Intent(this, DangKyActivity.class);
                            startActivity(iDangKy);
                        }
                    } else {
                        Log.w(TAG, "Lỗi kiểm tra quản lý: ", task.getException());
                        Toast.makeText(this, "Lỗi kết nối, không thể mở đăng ký", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//bỏ onDestroy do không còn DAO
    // TỐI ƯU: Luôn đóng kết nối CSDL khi Activity bị hủy
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (nhanVienDAO != null) {
//            nhanVienDAO.close();
//        }
//    }
}