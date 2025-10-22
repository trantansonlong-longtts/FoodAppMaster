package com.example.anhki.foodapp.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;
import com.example.anhki.foodapp.Database.CreateDatabase;

import java.util.ArrayList;
import java.util.List;

public class LoaiMonAnDAO {
    private final SQLiteDatabase database;
    private final CreateDatabase createDatabase; // Thêm biến này

    public LoaiMonAnDAO(Context context) {
        createDatabase = new CreateDatabase(context); // Khởi tạo
        database = createDatabase.open();
    }

    // TỐI ƯU: Thêm phương thức close() để quản lý tài nguyên
    public void close() {
        createDatabase.close();
    }

    // Sửa lại tham số thứ hai thành byte[]
    public boolean ThemLoaiMonAn(String tenloai, byte[] hinhanh) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_LOAIMONAN_TENLOAI, tenloai);
        contentValues.put(CreateDatabase.TB_LOAIMONAN_HINHANH, hinhanh); // Bây giờ nó sẽ lưu mảng byte

        long kiemtra = database.insert(CreateDatabase.TB_LOAIMONAN, null, contentValues);
        return kiemtra != -1;
    }
    public boolean XoaLoaiMonAn(int maloai) {
        String whereClause = CreateDatabase.TB_LOAIMONAN_MALOAI + " = ?";
        String[] whereArgs = {String.valueOf(maloai)};
        int rowsAffected = database.delete(CreateDatabase.TB_LOAIMONAN, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public List<LoaiMonAnDTO> LayDanhSachLoaiMonAn() {
        List<LoaiMonAnDTO> loaiMonAnDTOs = new ArrayList<>();
        String truyvan = "SELECT * FROM " + CreateDatabase.TB_LOAIMONAN;

        Cursor cursor = null; // Khai báo ngoài try
        try {
            cursor = database.rawQuery(truyvan, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Tối ưu: Lấy chỉ số cột 1 lần ngoài vòng lặp
                int colMaLoai = cursor.getColumnIndex(CreateDatabase.TB_LOAIMONAN_MALOAI);
                int colTenLoai = cursor.getColumnIndex(CreateDatabase.TB_LOAIMONAN_TENLOAI);
                int colHinhAnh = cursor.getColumnIndex(CreateDatabase.TB_LOAIMONAN_HINHANH);

                do {
                    LoaiMonAnDTO loaiMonAnDTO = new LoaiMonAnDTO();
                    loaiMonAnDTO.setMaLoai(cursor.getInt(colMaLoai));
                    loaiMonAnDTO.setTenLoai(cursor.getString(colTenLoai));
                    //tạm bỏ
                    //loaiMonAnDTO.setHinhAnh(cursor.getBlob(colHinhAnh)); // Lấy thêm hình ảnh
                    loaiMonAnDTOs.add(loaiMonAnDTO);
                } while (cursor.moveToNext());
            }
        } finally {
            // Tối ưu: Luôn đóng cursor để chống rò rỉ bộ nhớ
            if (cursor != null) {
                cursor.close();
            }
        }
        return loaiMonAnDTOs;
    }
    // 1. THÊM PHƯƠNG THỨC NÀY VÀO
    @SuppressLint("Range")
    public LoaiMonAnDTO LayLoaiMonAnTheoMa(int maloai){
        LoaiMonAnDTO loaiMonAnDTO = null;
        String query = "SELECT * FROM " + CreateDatabase.TB_LOAIMONAN + " WHERE " + CreateDatabase.TB_LOAIMONAN_MALOAI + " = ?";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{String.valueOf(maloai)});
            if(cursor != null && cursor.moveToFirst()){
                loaiMonAnDTO = new LoaiMonAnDTO();
                loaiMonAnDTO.setMaLoai(cursor.getInt(cursor.getColumnIndex(CreateDatabase.TB_LOAIMONAN_MALOAI)));
                loaiMonAnDTO.setTenLoai(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_LOAIMONAN_TENLOAI)));
                //loaiMonAnDTO.setHinhAnh(cursor.getBlob(cursor.getColumnIndex(CreateDatabase.TB_LOAIMONAN_HINHANH)));
            }
        } finally {
            if(cursor != null) cursor.close();
        }
        return loaiMonAnDTO;
    }
    @SuppressLint("Range")
    public String LayTenLoaiTheoMa(int maloai){
        String tenloai = "";
        String query = "SELECT " + CreateDatabase.TB_LOAIMONAN_TENLOAI + " FROM " + CreateDatabase.TB_LOAIMONAN + " WHERE " + CreateDatabase.TB_LOAIMONAN_MALOAI + " = ?";
        Cursor cursor = null;
        try{
            cursor = database.rawQuery(query, new String[]{String.valueOf(maloai)});
            if(cursor != null && cursor.moveToFirst()){
                tenloai = cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_LOAIMONAN_TENLOAI));
            }
        } finally {
            if(cursor != null) cursor.close();
        }
        return tenloai;
    }

    public boolean CapNhatLoaiMonAn(LoaiMonAnDTO loaiMonAnDTO) {
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.TB_LOAIMONAN_TENLOAI, loaiMonAnDTO.getTenLoai());
        values.put(CreateDatabase.TB_LOAIMONAN_HINHANH, loaiMonAnDTO.getHinhAnh());

        String whereClause = CreateDatabase.TB_LOAIMONAN_MALOAI + " = ?";
        String[] whereArgs = {String.valueOf(loaiMonAnDTO.getMaLoai())};

        int rowsAffected = database.update(CreateDatabase.TB_LOAIMONAN, values, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    public boolean KiemTraTrungTenLoai(String tenLoai) {
        String query = "SELECT 1 FROM " + CreateDatabase.TB_LOAIMONAN +
                " WHERE " + CreateDatabase.TB_LOAIMONAN_TENLOAI + " = ? LIMIT 1";

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{tenLoai});
            // An toàn: Luôn kiểm tra cursor không null
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}