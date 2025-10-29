package com.example.anhki.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 3000; // 3 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Dùng Handler để tạo độ trễ, cách làm hiện đại và an toàn hơn Thread
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent iDangNhap = new Intent(SplashScreenActivity.this, DangNhapActivity.class);
            startActivity(iDangNhap);
            finish(); // Đóng màn hình splash
        }, SPLASH_DELAY);
    }
}