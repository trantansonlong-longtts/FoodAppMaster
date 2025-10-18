package com.example.anhki.foodapp.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anhki.foodapp.DTO.QuyenDTO;
import com.example.anhki.foodapp.Database.CreateDatabase;

import java.util.ArrayList;
import java.util.List;

public class QuyenDAO {
    private final SQLiteDatabase database;
    private final CreateDatabase createDatabase; // Thêm biến này để quản lý đóng/mở

    public QuyenDAO(Context context) {
        createDatabase = new CreateDatabase(context);
        database = createDatabase.open();
    }

    // TỐI ƯU: Thêm phương thức close() để quản lý tài nguyên
    public void close() {
        createDatabase.close();
    }

    // TỐI ƯU: Trả về boolean để biết việc thêm có thành công hay không
    public boolean ThemQuyen(String tenquyen) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_QUYEN_TENQUYEN, tenquyen);

        // database.insert() trả về -1 nếu có lỗi
        long check = database.insert(CreateDatabase.TB_QUYEN, null, contentValues);
        return check != -1;
    }
    /**
     * Lấy tên quyền dựa vào mã quyền.
     * @param maquyen Mã quyền cần tìm.
     * @return Chuỗi tên quyền.
     */
    @SuppressLint("Range")
    public String LayTenQuyenTheoMa(int maquyen) {
        String tenQuyen = "";
        String query = "SELECT " + CreateDatabase.TB_QUYEN_TENQUYEN + " FROM " + CreateDatabase.TB_QUYEN + " WHERE " + CreateDatabase.TB_QUYEN_MAQUYEN + " = ?";

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{String.valueOf(maquyen)});
            if (cursor != null && cursor.moveToFirst()) {
                tenQuyen = cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_QUYEN_TENQUYEN));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tenQuyen;
    }

    @SuppressLint("Range")
    public List<QuyenDTO> LayDanhSachQuyen() {
        List<QuyenDTO> quyenDTOS = new ArrayList<>();
        String truyvan = "SELECT * FROM " + CreateDatabase.TB_QUYEN;

        Cursor cursor = null; // Khai báo ngoài khối try
        try {
            cursor = database.rawQuery(truyvan, null);
            if (cursor != null && cursor.moveToFirst()) {
                // TỐI ƯU: Lấy chỉ số cột 1 lần duy nhất ngoài vòng lặp để tăng hiệu suất
                int colMaQuyen = cursor.getColumnIndex(CreateDatabase.TB_QUYEN_MAQUYEN);
                int colTenQuyen = cursor.getColumnIndex(CreateDatabase.TB_QUYEN_TENQUYEN);

                do {
                    QuyenDTO quyenDTO = new QuyenDTO();

                    // SỬA LỖI NGHIÊM TRỌNG: Dùng đúng hằng số của bảng QUYEN
                    quyenDTO.setMaQuyen(cursor.getInt(colMaQuyen));
                    quyenDTO.setTenQuyen(cursor.getString(colTenQuyen));

                    quyenDTOS.add(quyenDTO);
                } while (cursor.moveToNext());
            }
        } finally {
            // TỐI ƯU: Luôn đóng cursor trong khối finally để chống rò rỉ bộ nhớ
            if (cursor != null) {
                cursor.close();
            }
        }

        return quyenDTOS;
    }
}
//Gemini
