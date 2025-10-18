package com.example.anhki.foodapp.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anhki.foodapp.Contants;
import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.Database.CreateDatabase;

import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    private final SQLiteDatabase database;
    private final CreateDatabase createDatabase;

    public NhanVienDAO(Context context) {
        createDatabase = new CreateDatabase(context);
        database = createDatabase.open();
    }

    public void close() {
        createDatabase.close();
    }

    // Thêm kiểm tra tồn tại của quyền quan lý chưa để gọi trang đăng ký
    public boolean KiemTraQuanLyTonTai() {
        // Giả sử mã quyền của Quản lý là 1 (Constants.QUYEN_QUANLY)
        String query = "SELECT 1 FROM " + CreateDatabase.TB_NHANVIEN + " WHERE " + CreateDatabase.TB_NHANVIEN_MAQUYEN + " = ? LIMIT 1";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{String.valueOf(Contants.QUYEN_QUANLY)});
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public boolean ThemNhanVien(NhanVienDTO nhanVienDTO) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_NHANVIEN_TEDN, nhanVienDTO.getTENDANGNHAP());
        contentValues.put(CreateDatabase.TB_NHANVIEN_MATKHAU, nhanVienDTO.getMATKHAU());
        contentValues.put(CreateDatabase.TB_NHANVIEN_GIOITINH, nhanVienDTO.getGIOITINH());
        contentValues.put(CreateDatabase.TB_NHANVIEN_NGAYSINH, nhanVienDTO.getNGAYSINH());
        contentValues.put(CreateDatabase.TB_NHANVIEN_CMND, nhanVienDTO.getCMND());
        contentValues.put(CreateDatabase.TB_NHANVIEN_MAQUYEN, nhanVienDTO.getMAQUYEN());

        long check = database.insert(CreateDatabase.TB_NHANVIEN, null, contentValues);
        return check != -1;
    }

    public boolean SuaNhanVien(NhanVienDTO nhanVienDTO) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_NHANVIEN_TEDN, nhanVienDTO.getTENDANGNHAP());
        contentValues.put(CreateDatabase.TB_NHANVIEN_MATKHAU, nhanVienDTO.getMATKHAU());
        contentValues.put(CreateDatabase.TB_NHANVIEN_GIOITINH, nhanVienDTO.getGIOITINH());
        contentValues.put(CreateDatabase.TB_NHANVIEN_NGAYSINH, nhanVienDTO.getNGAYSINH());
        contentValues.put(CreateDatabase.TB_NHANVIEN_CMND, nhanVienDTO.getCMND());
        contentValues.put(CreateDatabase.TB_NHANVIEN_MAQUYEN, nhanVienDTO.getMAQUYEN());

        String whereClause = CreateDatabase.TB_NHANVIEN_MANV + " = ?";
        String[] whereArgs = {String.valueOf(nhanVienDTO.getMANV())};

        int rowsAffected = database.update(CreateDatabase.TB_NHANVIEN, contentValues, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    public boolean XoaNhanVien(int manhanvien) {
        String whereClause = CreateDatabase.TB_NHANVIEN_MANV + " = ?";
        String[] whereArgs = {String.valueOf(manhanvien)};

        int rowsAffected = database.delete(CreateDatabase.TB_NHANVIEN, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    public boolean KiemTraNhanVien() {
        String truyvan = "SELECT 1 FROM " + CreateDatabase.TB_NHANVIEN + " LIMIT 1";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @SuppressLint("Range")
    public int KiemTraDangNhap(String tendangnhap, String matkhau) {
        String truyvan = "SELECT " + CreateDatabase.TB_NHANVIEN_MANV + " FROM " + CreateDatabase.TB_NHANVIEN + " WHERE "
                + CreateDatabase.TB_NHANVIEN_TEDN + " = ? AND "
                + CreateDatabase.TB_NHANVIEN_MATKHAU + " = ?";
        String[] selectionArgs = {tendangnhap, matkhau};

        int manhanvien = 0; // Trả về 0 nếu đăng nhập thất bại
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                manhanvien = cursor.getInt(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_MANV));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return manhanvien;
    }

    @SuppressLint("Range")
    public List<NhanVienDTO> LayDanhSachNhanVien() {
        List<NhanVienDTO> nhanvienDTOS = new ArrayList<>();
        String truyvan = "SELECT * FROM " + CreateDatabase.TB_NHANVIEN;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, null);
            if (cursor != null && cursor.moveToFirst()) {
                int colMaNV = cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_MANV);
                int colTenDN = cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_TEDN);
                int colMatKhau = cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_MATKHAU);
                int colGioiTinh = cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_GIOITINH);
                int colNgaySinh = cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_NGAYSINH);
                int colCMND = cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_CMND);
                int colMaQuyen = cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_MAQUYEN);

                do {
                    NhanVienDTO nhanVienDTO = new NhanVienDTO();
                    nhanVienDTO.setMANV(cursor.getInt(colMaNV));
                    nhanVienDTO.setTENDANGNHAP(cursor.getString(colTenDN));
                    nhanVienDTO.setMATKHAU(cursor.getString(colMatKhau));
                    nhanVienDTO.setGIOITINH(cursor.getString(colGioiTinh));
                    nhanVienDTO.setNGAYSINH(cursor.getString(colNgaySinh));
                    nhanVienDTO.setCMND(cursor.getString(colCMND));
                    nhanVienDTO.setMAQUYEN(cursor.getInt(colMaQuyen));
                    nhanvienDTOS.add(nhanVienDTO);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return nhanvienDTOS;
    }

    @SuppressLint("Range")
    public NhanVienDTO LayDanhSachNhanVienTheoMa(int manhanvien) {
        NhanVienDTO nhanVienDTO = null; // Trả về null nếu không tìm thấy
        String truyvan = "SELECT * FROM " + CreateDatabase.TB_NHANVIEN + " WHERE " + CreateDatabase.TB_NHANVIEN_MANV + " = ?";
        String[] selectionArgs = {String.valueOf(manhanvien)};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                nhanVienDTO = new NhanVienDTO();
                nhanVienDTO.setMANV(cursor.getInt(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_MANV)));
                nhanVienDTO.setTENDANGNHAP(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_TEDN)));
                nhanVienDTO.setMATKHAU(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_MATKHAU)));
                nhanVienDTO.setGIOITINH(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_GIOITINH)));
                nhanVienDTO.setNGAYSINH(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_NGAYSINH)));
                nhanVienDTO.setCMND(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_CMND)));
                nhanVienDTO.setMAQUYEN(cursor.getInt(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_MAQUYEN)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return nhanVienDTO;
    }

    @SuppressLint("Range")
    public int LayQuyenNhanVien(int manv) {
        int maquyen = -1; // Trả về -1 nếu không tìm thấy
        String truyvan = "SELECT " + CreateDatabase.TB_NHANVIEN_MAQUYEN + " FROM " + CreateDatabase.TB_NHANVIEN + " WHERE " + CreateDatabase.TB_NHANVIEN_MANV + " = ?";
        String[] selectionArgs = {String.valueOf(manv)};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                maquyen = cursor.getInt(cursor.getColumnIndex(CreateDatabase.TB_NHANVIEN_MAQUYEN));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return maquyen;
    }
}