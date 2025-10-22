
package com.example.anhki.foodapp.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anhki.foodapp.DTO.MonAnDTO;
import com.example.anhki.foodapp.Database.CreateDatabase;

import java.util.ArrayList;
import java.util.List;

public class MonAnDAO {
    private final SQLiteDatabase database;
    private final CreateDatabase createDatabase;
    public MonAnDAO(Context context) {
        createDatabase = new CreateDatabase(context);
        database = createDatabase.open();
    }
    public void close() {
        createDatabase.close();
    }

    // ================== THÊM MÓN ĂN ==================
    public boolean ThemMonAn(MonAnDTO monAnDTO) {
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.TB_MONAN_TENMONAN, monAnDTO.getTenMonAn());
        values.put(CreateDatabase.TB_MONAN_GIATIEN, monAnDTO.getGiaTien());
        values.put(CreateDatabase.TB_MONAN_MALOAI, monAnDTO.getMaLoai());
        values.put(CreateDatabase.TB_MONAN_HINHANH, monAnDTO.getHinhAnh());

        long result = database.insert(CreateDatabase.TB_MONAN, null, values);
        return result != -1;
    }

    // ================== LẤY DANH SÁCH THEO LOẠI ==================
    // TỐI ƯU LẠI PHƯƠNG THỨC NÀY
    @SuppressLint("Range")
    public List<MonAnDTO> LayDanhSachMonAnTheoLoai(int maloai) {
        List<MonAnDTO> monAnDTOs = new ArrayList<>();
        String sql = "SELECT * FROM " + CreateDatabase.TB_MONAN +
                " WHERE " + CreateDatabase.TB_MONAN_MALOAI + " = ?";

        Cursor cursor = null; // Khai báo ngoài try
        try {
            cursor = database.rawQuery(sql, new String[]{String.valueOf(maloai)});
            if (cursor != null && cursor.moveToFirst()) {
                // Tối ưu: Lấy chỉ số cột 1 lần ngoài vòng lặp
                int colMaMon = cursor.getColumnIndex(CreateDatabase.TB_MONAN_MAMON);
                int colTenMon = cursor.getColumnIndex(CreateDatabase.TB_MONAN_TENMONAN);
                int colGiaTien = cursor.getColumnIndex(CreateDatabase.TB_MONAN_GIATIEN);
                int colMaLoai = cursor.getColumnIndex(CreateDatabase.TB_MONAN_MALOAI);
                int colHinhAnh = cursor.getColumnIndex(CreateDatabase.TB_MONAN_HINHANH);

                do {
                    MonAnDTO monAnDTO = new MonAnDTO();
                    monAnDTO.setMaMonAn(cursor.getInt(colMaMon));
                    monAnDTO.setTenMonAn(cursor.getString(colTenMon));
                    monAnDTO.setGiaTien(cursor.getInt(colGiaTien));
                    monAnDTO.setMaLoai(cursor.getInt(colMaLoai));
                    //monAnDTO.setHinhAnh(cursor.getBlob(colHinhAnh));
                    monAnDTOs.add(monAnDTO);
                } while (cursor.moveToNext());
            }
        } finally {
            // An toàn: Luôn đóng cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return monAnDTOs;
    }


    // ================== LẤY MÓN THEO ID ==================
    public MonAnDTO LayMonAnTheoId(int mamon) {
        MonAnDTO monAnDTO = null;
        String sql = "SELECT * FROM " + CreateDatabase.TB_MONAN +
                " WHERE " + CreateDatabase.TB_MONAN_MAMON + " = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(mamon)});

        if (cursor.moveToFirst()) {
            monAnDTO = new MonAnDTO();
            monAnDTO.setMaMonAn(cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.TB_MONAN_MAMON)));
            monAnDTO.setTenMonAn(cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.TB_MONAN_TENMONAN)));
            monAnDTO.setGiaTien(cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.TB_MONAN_GIATIEN)));
            monAnDTO.setMaLoai(cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.TB_MONAN_MALOAI)));
            //monAnDTO.setHinhAnh(cursor.getBlob(cursor.getColumnIndexOrThrow(CreateDatabase.TB_MONAN_HINHANH)));
        }
        cursor.close();
        return monAnDTO;
    }

    // ================== CẬP NHẬT MÓN ĂN ==================
    public boolean CapNhatMonAn(MonAnDTO monAnDTO) {
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.TB_MONAN_TENMONAN, monAnDTO.getTenMonAn());
        values.put(CreateDatabase.TB_MONAN_GIATIEN, monAnDTO.getGiaTien());
        values.put(CreateDatabase.TB_MONAN_MALOAI, monAnDTO.getMaLoai());
        values.put(CreateDatabase.TB_MONAN_HINHANH, monAnDTO.getHinhAnh());

        int rows = database.update(CreateDatabase.TB_MONAN,
                values,
                CreateDatabase.TB_MONAN_MAMON + " = ?",
                new String[]{String.valueOf(monAnDTO.getMaMonAn())});

        return rows > 0;
    }
    public boolean KiemTraMonAnTonTaiTrongLoai(int maloai) {
        String query = "SELECT 1 FROM " + CreateDatabase.TB_MONAN + " WHERE " + CreateDatabase.TB_MONAN_MALOAI + " = ? LIMIT 1";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{String.valueOf(maloai)});
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // ================== XÓA MÓN ĂN ==================
    public boolean XoaMonAn(int mamon) {
        int rows = database.delete(CreateDatabase.TB_MONAN,
                CreateDatabase.TB_MONAN_MAMON + " = ?",
                new String[]{String.valueOf(mamon)});
        return rows > 0;
    }
}

