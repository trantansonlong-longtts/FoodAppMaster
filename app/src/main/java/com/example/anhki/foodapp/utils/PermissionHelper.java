package com.example.anhki.foodapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Lớp helper dùng chung để xin quyền trong ứng dụng
 */
public class PermissionHelper {
    public static final int STORAGE_PERMISSION_CODE = 1;

    /**
     * Kiểm tra và xin quyền bộ nhớ
     */
    public static void requestStoragePermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(activity)
                        .setTitle("Ứng dụng cần được cấp quyền")
                        .setMessage("Ứng dụng cần được cấp quyền truy cập bộ nhớ để sử dụng tốt hơn!")
                        .setPositiveButton("Ok", (dialog, which) ->
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        STORAGE_PERMISSION_CODE))
                        .setNegativeButton("Hủy", (dialog, which) -> activity.finish())
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            }
        }
    }

    /**
     * Xử lý kết quả xin quyền
     */
    public static void handlePermissionResult(Activity activity,
                                              int requestCode,
                                              @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Đã được cấp quyền!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Ứng dụng bị từ chối cấp quyền!", Toast.LENGTH_SHORT).show();
                requestStoragePermission(activity);
            }
        }
    }
}
