//bản gemini
package com.example.anhki.foodapp.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.anhki.foodapp.DTO.ChiTietGoiMonDTO;
import com.example.anhki.foodapp.DTO.GoiMonDTO;
import com.example.anhki.foodapp.DTO.ThanhToanDTO;
import com.example.anhki.foodapp.Database.CreateDatabase;

import java.util.ArrayList;
import java.util.List;

public class GoiMonDAO {
    private final SQLiteDatabase database;
    private final CreateDatabase createDatabase;

    public GoiMonDAO(Context context) {
        createDatabase = new CreateDatabase(context);
        database = createDatabase.open();
    }

    public void close() {
        createDatabase.close();
    }

    public long ThemGoiMon(GoiMonDTO goiMonDTO) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_GOIMON_MABAN, goiMonDTO.getMaBan());
        contentValues.put(CreateDatabase.TB_GOIMON_MANV, goiMonDTO.getMaNhanVien());
        contentValues.put(CreateDatabase.TB_GOIMON_NGAYGOI, goiMonDTO.getNgayGoi());
        contentValues.put(CreateDatabase.TB_GOIMON_TINHTRANG, goiMonDTO.getTinhTrang());

        // Trả về row ID hoặc -1 nếu lỗi, không cần kiểm tra thêm
        return database.insert(CreateDatabase.TB_GOIMON, null, contentValues);
    }

    public long LayMaGoiMonTheoMaBan(int maban, String tinhtrang) {
        String truyvan = "SELECT * FROM " + CreateDatabase.TB_GOIMON + " WHERE " + CreateDatabase.TB_GOIMON_MABAN + " = ? AND "
                + CreateDatabase.TB_GOIMON_TINHTRANG + " = ?";
        String[] selectionArgs = {String.valueOf(maban), tinhtrang};

        long magoimon = 0;
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, selectionArgs);
            // Một bàn chỉ có 1 gọi món chưa thanh toán, nên dùng if, không dùng while
            if (cursor != null && cursor.moveToFirst()) {
                magoimon = cursor.getLong(cursor.getColumnIndexOrThrow(CreateDatabase.TB_GOIMON_MAGOIMON));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return magoimon;
    }

    public boolean KiemTraMonAnDaTonTai(int magoimon, int mamonan) {
        // Tối ưu: Chỉ cần SELECT 1 dòng để kiểm tra tồn tại
        String truyvan = "SELECT 1 FROM " + CreateDatabase.TB_CHITIETGOIMON + " WHERE " + CreateDatabase.TB_CHITIETGOIMON_MAMONAN
                + " = ? AND " + CreateDatabase.TB_CHITIETGOIMON_MAGOIMON + " = ? LIMIT 1";
        String[] selectionArgs = {String.valueOf(mamonan), String.valueOf(magoimon)};
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, selectionArgs);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int LaySoLuongMonAnTheoMaGoiMon(int magoimon, int mamonan) {
        int soluong = 0;
        String truyvan = "SELECT " + CreateDatabase.TB_CHITIETGOIMON_SOLUONG + " FROM " + CreateDatabase.TB_CHITIETGOIMON + " WHERE " + CreateDatabase.TB_CHITIETGOIMON_MAMONAN
                + " = ? AND " + CreateDatabase.TB_CHITIETGOIMON_MAGOIMON + " = ?";
        String[] selectionArgs = {String.valueOf(mamonan), String.valueOf(magoimon)};

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                soluong = cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.TB_CHITIETGOIMON_SOLUONG));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return soluong;
    }

    public boolean CapNhatSoLuong(ChiTietGoiMonDTO chiTietGoiMonDTO) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_CHITIETGOIMON_SOLUONG, chiTietGoiMonDTO.getSoLuong());

        String whereClause = CreateDatabase.TB_CHITIETGOIMON_MAGOIMON + " = ? AND " + CreateDatabase.TB_CHITIETGOIMON_MAMONAN + " = ?";
        String[] whereArgs = {String.valueOf(chiTietGoiMonDTO.getMaGoiMon()), String.valueOf(chiTietGoiMonDTO.getMaMonAn())};

        int rowsAffected = database.update(CreateDatabase.TB_CHITIETGOIMON, contentValues, whereClause, whereArgs);
        return rowsAffected > 0; // update trả về số dòng bị ảnh hưởng
    }

    public boolean ThemChiTietGoiMon(ChiTietGoiMonDTO chiTietGoiMonDTO) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_CHITIETGOIMON_SOLUONG, chiTietGoiMonDTO.getSoLuong());
        contentValues.put(CreateDatabase.TB_CHITIETGOIMON_MAGOIMON, chiTietGoiMonDTO.getMaGoiMon());
        contentValues.put(CreateDatabase.TB_CHITIETGOIMON_MAMONAN, chiTietGoiMonDTO.getMaMonAn());

        long rowId = database.insert(CreateDatabase.TB_CHITIETGOIMON, null, contentValues);
        return rowId != -1; // insert trả về -1 nếu lỗi
    }

    @SuppressLint("Range")
    public List<ThanhToanDTO> LayDanhSachMonAnTheoMaGoiMon(int magoimon) {
        // Tối ưu: Dùng cú pháp INNER JOIN hiện đại, rõ ràng hơn
        String truyvan = "SELECT ct." + CreateDatabase.TB_CHITIETGOIMON_SOLUONG + ", ma." + CreateDatabase.TB_MONAN_GIATIEN + ", ma." + CreateDatabase.TB_MONAN_TENMONAN
                + " FROM " + CreateDatabase.TB_CHITIETGOIMON + " ct INNER JOIN " + CreateDatabase.TB_MONAN + " ma ON "
                + "ct." + CreateDatabase.TB_CHITIETGOIMON_MAMONAN + " = ma." + CreateDatabase.TB_MONAN_MAMON
                + " WHERE ct." + CreateDatabase.TB_CHITIETGOIMON_MAGOIMON + " = ?";
        String[] selectionArgs = {String.valueOf(magoimon)};

        List<ThanhToanDTO> thanhToanDTOS = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                // Tối ưu: Lấy chỉ số cột 1 lần ngoài vòng lặp
                int colSoLuong = cursor.getColumnIndex(CreateDatabase.TB_CHITIETGOIMON_SOLUONG);
                int colGiaTien = cursor.getColumnIndex(CreateDatabase.TB_MONAN_GIATIEN);
                int colTenMon = cursor.getColumnIndex(CreateDatabase.TB_MONAN_TENMONAN);

                do {
                    ThanhToanDTO thanhToanDTO = new ThanhToanDTO();
                    thanhToanDTO.setSoLuong(cursor.getInt(colSoLuong));
                    thanhToanDTO.setGiatien(cursor.getInt(colGiaTien));
                    thanhToanDTO.setTenMonAn(cursor.getString(colTenMon));
                    thanhToanDTOS.add(thanhToanDTO);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return thanhToanDTOS;
    }

    public boolean CapNhatTrangThaiGoiMonTheoMaBan(int maban, String tinhtrang) {
        ContentValues contentValues = new ContentValues();
        // SỬA LỖI NGHIÊM TRỌNG: Phải put "tinhtrang", không phải "maban"
        contentValues.put(CreateDatabase.TB_GOIMON_TINHTRANG, tinhtrang);

        String whereClause = CreateDatabase.TB_GOIMON_MABAN + " = ?";
        String[] whereArgs = {String.valueOf(maban)};

        int rowsAffected = database.update(CreateDatabase.TB_GOIMON, contentValues, whereClause, whereArgs);
        return rowsAffected > 0;
    }
}