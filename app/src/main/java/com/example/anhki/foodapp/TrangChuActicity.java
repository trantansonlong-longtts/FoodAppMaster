package com.example.anhki.foodapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.anhki.foodapp.Fragment.HienThiBanAnFragment;
import com.example.anhki.foodapp.Fragment.HienThiNhanVienFragment;
import com.example.anhki.foodapp.Fragment.HienThiThucDonFragment;
import com.example.anhki.foodapp.utils.PermissionHelper;
import com.google.android.material.navigation.NavigationView;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TrangChuActicity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView txtTenNhanVien_Navigation;
    private FragmentManager fragmentManager;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_trangchu);

        // Xin quyền bộ nhớ
        PermissionHelper.requestStoragePermission(this);

        // Ánh xạ view
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationview_trangchu);
        toolbar = findViewById(R.id.toolbar);

        // Inflate header để lấy TextView
        View headerView = navigationView.inflateHeaderView(R.layout.layout_header_navigation_trangchu);
        txtTenNhanVien_Navigation = headerView.findViewById(R.id.txtTenNhanVienNavigation);

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup DrawerToggle
        androidx.appcompat.app.ActionBarDrawerToggle drawerToggle =
                new androidx.appcompat.app.ActionBarDrawerToggle(
                        this, drawerLayout, toolbar,
                        R.string.mo, R.string.dong
                );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Setup NavigationView
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        // Nhận dữ liệu từ DangNhapActivity
        Intent intent = getIntent();
        String tendn = intent.getStringExtra("tendn");
        txtTenNhanVien_Navigation.setText(tendn != null ? tendn : "Khách");

        // Load fragment mặc định: Bàn ăn
        fragmentManager = getSupportFragmentManager();
        replaceFragment(new HienThiBanAnFragment());
        toolbar.setTitle("Bàn Ăn");
        navigationView.setCheckedItem(R.id.itTrangChu);
    }

    // Hàm mở activity sửa món ăn
//    public void moSuaMonAn(int mamon) {
//        Intent intent = new Intent(this, SuaThucDonActivity.class);
//        intent.putExtra("mamon", mamon);
//        startActivity(intent);
//        overridePendingTransition(R.anim.hieuung_activity_vao, R.anim.hieuung_activity_ra);
//    }

    /**
     * Callback xin quyền
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.handlePermissionResult(this, requestCode, grantResults);
    }

    /**
     * Hàm thay fragment gọn gàng
     */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.hieuung_activity_vao, R.anim.hieuung_activity_ra);
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // phương thức xử lý đăng xuất

    private void xuLyDangXuat() {
        // 1. Xóa quyền đã lưu trong SharedPreferences
        // Đảm bảo bạn dùng đúng tên file SharedPreferences đã tạo lúc đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa tất cả dữ liệu đã lưu (an toàn nhất)
        editor.apply();

        // 2. Tạo Intent để quay về màn hình đăng nhập
        Intent intent = new Intent(TrangChuActicity.this, DangNhapActivity.class);

        // 3. Cài đặt các cờ (flag) đặc biệt
        // Xóa sạch tất cả các Activity cũ và tạo một tác vụ mới cho màn hình đăng nhập.
        // Điều này ngăn người dùng nhấn nút "Back" để quay lại TrangChuActivity sau khi đã đăng xuất.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // 4. Bắt đầu Activity mới và kết thúc Activity hiện tại
        startActivity(intent);
        finish();
    }
    /**
     * Xử lý click navigation item
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.itTrangChu) {
            replaceFragment(new HienThiBanAnFragment());
            toolbar.setTitle("Bàn ăn");
        } else if (id == R.id.itThucDon) {
            replaceFragment(new HienThiThucDonFragment());
            toolbar.setTitle("Thực đơn");
        } else if (id == R.id.itNhanVien) {
            replaceFragment(new HienThiNhanVienFragment());
            toolbar.setTitle("Nhân viên");
        } else if (id == R.id.itDangXuat) {
            xuLyDangXuat();
        }

        item.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }
}


