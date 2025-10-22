package com.example.anhki.foodapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.anhki.foodapp.DAO.QuyenDAO;
import com.example.anhki.foodapp.DTO.QuyenDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuaNhanVienActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SuaNhanVienActivity";

    private EditText edTenDangNhap, edCMND, edNgaySinh;
    private RadioGroup rgGioiTinh;
    private Spinner spinQuyen;
    private Button btnLuu, btnThoat;

    private FirebaseFirestore db;
    private QuyenDAO quyenDAO;
    private List<QuyenDTO> quyenDTOList;

    private String uid; // UID của nhân viên cần sửa

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suanhanvien);

        db = FirebaseFirestore.getInstance();
        quyenDAO = new QuyenDAO(this);

        anhXaView();

        btnLuu.setOnClickListener(this);
        btnThoat.setOnClickListener(this);
        edNgaySinh.setOnClickListener(this);

        hienThiDanhSachQuyen();

        uid = getIntent().getStringExtra("uid");
        if (uid != null && !uid.isEmpty()) {
            loadNhanVienData();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy nhân viên", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (quyenDAO != null) quyenDAO.close();
    }

    private void anhXaView() {
        edTenDangNhap = findViewById(R.id.edSuaTenDangNhap);
        edCMND = findViewById(R.id.edSuaCMND);
        edNgaySinh = findViewById(R.id.edSuaNgaySinh);
        rgGioiTinh = findViewById(R.id.rgSuaGioiTinh);
        spinQuyen = findViewById(R.id.spinSuaQuyen);
        btnLuu = findViewById(R.id.btnLuuSuaNV);
        btnThoat = findViewById(R.id.btnThoatSuaNV);
    }

    private void hienThiDanhSachQuyen() {
        quyenDTOList = quyenDAO.LayDanhSachQuyen();
        List<String> dataAdapter = new ArrayList<>();
        for (QuyenDTO quyen : quyenDTOList) {
            dataAdapter.add(quyen.getTenQuyen());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dataAdapter);
        spinQuyen.setAdapter(adapter);
    }

    private void loadNhanVienData() {
        DocumentReference docRef = db.collection("nhanVien").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    edTenDangNhap.setText(document.getString("tenDangNhap"));
                    edCMND.setText(document.getString("cmnd"));
                    edNgaySinh.setText(document.getString("ngaySinh"));

                    String gioiTinh = document.getString("gioiTinh");
                    if ("Nam".equals(gioiTinh)) {
                        rgGioiTinh.check(R.id.rdSuaNam);
                    } else {
                        rgGioiTinh.check(R.id.rdSuaNu);
                    }

                    Long maQuyenLong = document.getLong("maQuyen");
                    int maQuyenCurrent = (maQuyenLong != null) ? maQuyenLong.intValue() : -1;
                    for (int i = 0; i < quyenDTOList.size(); i++) {
                        if (quyenDTOList.get(i).getMaQuyen() == maQuyenCurrent) {
                            spinQuyen.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(this, "Không tìm thấy dữ liệu nhân viên", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void luuThayDoi() {
        String cmnd = edCMND.getText().toString().trim();
        String ngaySinh = edNgaySinh.getText().toString().trim();
        String gioiTinh = (rgGioiTinh.getCheckedRadioButtonId() == R.id.rdSuaNam) ? "Nam" : "Nữ";
        int maQuyen;

        if (quyenDTOList == null || quyenDTOList.isEmpty()){
            Toast.makeText(this, "Lỗi: Không có danh sách quyền", Toast.LENGTH_SHORT).show();
            return;
        }
        int vitri = spinQuyen.getSelectedItemPosition();
        maQuyen = quyenDTOList.get(vitri).getMaQuyen();

        if (rgGioiTinh.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Map chứa các trường cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("cmnd", cmnd);
        updates.put("ngaySinh", ngaySinh);
        updates.put("gioiTinh", gioiTinh);
        updates.put("maQuyen", maQuyen);

        // Cập nhật document trên Firestore
        db.collection("nhanVien").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cập nhật Firestore thành công!");
                    Toast.makeText(SuaNhanVienActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi cập nhật Firestore", e);
                    Toast.makeText(SuaNhanVienActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                });
    }


    @SuppressLint("NewApi")
    private void chooseDay() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edNgaySinh.setText(date);
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLuuSuaNV) {
            luuThayDoi();
        } else if (id == R.id.btnThoatSuaNV) {
            finish();
        } else if (id == R.id.edSuaNgaySinh) {
            chooseDay();
        }
    }
}