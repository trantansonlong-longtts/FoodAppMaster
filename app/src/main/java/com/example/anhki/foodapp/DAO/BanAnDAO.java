//package com.example.anhki.foodapp.DAO;
//
//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.example.anhki.foodapp.DTO.BanAnDTO;
//import com.example.anhki.foodapp.Database.CreateDatabase;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BanAnDAO {
//    private final SQLiteDatabase database;
//
//    public BanAnDAO(Context context){
//        CreateDatabase createDatabase = new CreateDatabase(context);
//        database = createDatabase.open();
//    }
//
//    public boolean ThemBanAn(String tenban){
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(CreateDatabase.TB_BANAN_TENBAN, tenban);
//        contentValues.put(CreateDatabase.TB_BANAN_TINHTRANG, "false");
//
//        long kiemtra = database.insert(CreateDatabase.TB_BANAN, null, contentValues);
//        return kiemtra != 0;
//    }
//
//    @SuppressLint("Recycle")
//    public List<BanAnDTO> LayTatCaBanAn(){
//        List<BanAnDTO> banAnDTOList = new ArrayList<>();
//        String truyvan= "SELECT * FROM " + CreateDatabase.TB_BANAN;
//        Cursor cursor = database.rawQuery(truyvan, null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()){
//            BanAnDTO banAnDTO = new BanAnDTO();
//            banAnDTO.setMaBan(cursor.getInt(cursor.getColumnIndex(CreateDatabase.TB_BANAN_MABAN)));
//            banAnDTO.setTenBan(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_BANAN_TENBAN)));
//
//            banAnDTOList.add(banAnDTO);
//            cursor.moveToNext();
//        }
//        return banAnDTOList;
//    }
//
//    @SuppressLint("Recycle")
//    public String LayTinhTrangBan(int maban){
//        String tinhtrang = "";
//        String truyvan = "SELECT * FROM " + CreateDatabase.TB_BANAN + " WHERE " + CreateDatabase.TB_BANAN_MABAN + " = '" + maban + "'";
//        Cursor cursor = database.rawQuery(truyvan, null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()){
//            tinhtrang = cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_BANAN_TINHTRANG));
//            cursor.moveToNext();
//        }
//
//        return tinhtrang;
//    }
//
//    public boolean CapNhatTinhTrangBan(int maban, String tinhtrang){
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(CreateDatabase.TB_BANAN_TINHTRANG, tinhtrang);
//
//        long kiemtra = database.update(CreateDatabase.TB_BANAN, contentValues, CreateDatabase.TB_BANAN_MABAN + " = '" + maban + "'", null);
//        return kiemtra != 0;
//    }
//
//    public boolean CapNhatTenBan(int maban, String tenban){
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(CreateDatabase.TB_BANAN_TENBAN, tenban);
//
//        long kiemtra = database.update(CreateDatabase.TB_BANAN, contentValues, CreateDatabase.TB_BANAN_MABAN + " = '" + maban + "'", null);
//        return kiemtra != 0;
//    }
//    //hien thi ten ban cu khi sưa
//    public String LayTenBan(int maban) {
//        String tenBan = "";
//        Cursor cursor = database.rawQuery("SELECT TenBan FROM " + CreateDatabase.TB_BANAN +
//                " WHERE " + CreateDatabase.TB_BANAN_MABAN + " = ?", new String[]{String.valueOf(maban)});
//        if (cursor.moveToFirst()) {
//            tenBan = cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.TB_BANAN_TENBAN));
//        }
//        cursor.close();
//        return tenBan;
//    }
//
//
//    public boolean XoaBanAn(int maban){
//        long kiemtra = database.delete(CreateDatabase.TB_BANAN, CreateDatabase.TB_BANAN_MABAN + " = " + maban, null);
//        return kiemtra != 0;
//    }
////}


//bản Gemini

package com.example.anhki.foodapp.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.anhki.foodapp.DTO.BanAnDTO;
import com.example.anhki.foodapp.Database.CreateDatabase;

import java.util.ArrayList;
import java.util.List;

public class BanAnDAO {
    private final SQLiteDatabase database;
    private final CreateDatabase createDatabase;

    public BanAnDAO(Context context) {
        // Khởi tạo một lần và tái sử dụng
        createDatabase = new CreateDatabase(context);
        database = createDatabase.open();
    }

    // Nên có một phương thức để đóng kết nối khi không cần thiết nữa
    // Ví dụ: gọi phương thức này trong onDestroy() của Activity/Fragment
    public void close() {
        createDatabase.close();
    }

    /**
     * Thêm một bàn ăn mới vào cơ sở dữ liệu.
     * @param tenban Tên của bàn ăn.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean ThemBanAn(String tenban) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_BANAN_TENBAN, tenban);
        // Mặc định tình trạng là "false" khi mới tạo
        contentValues.put(CreateDatabase.TB_BANAN_TINHTRANG, "false");

        // database.insert() trả về -1 nếu có lỗi
        long kiemtra = database.insert(CreateDatabase.TB_BANAN, null, contentValues);
        if (kiemtra == -1) {
            Log.e("BanAnDAO", "Lỗi khi thêm bàn: " + tenban);
        }
        return kiemtra != -1;
    }

    /**
     * Kiểm tra xem tên bàn ăn đã tồn tại hay chưa (bỏ qua khoảng trắng và không phân biệt hoa thường).
     * @param tenBan Tên bàn cần kiểm tra.
     * @return true nếu tên đã tồn tại, false nếu chưa.
     */
    public boolean KiemTraTenBanAnTonTai(String tenBan) {
        // TRIM(?) để loại bỏ khoảng trắng và UPPER(?) để so sánh không phân biệt hoa thường
        String query = "SELECT 1 FROM " + CreateDatabase.TB_BANAN + " WHERE UPPER(TRIM(" + CreateDatabase.TB_BANAN_TENBAN + ")) = UPPER(TRIM(?))";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{tenBan});
            // Nếu cursor có ít nhất một dòng, tức là tên đã tồn tại
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Lấy danh sách tất cả các bàn ăn từ cơ sở dữ liệu.
     * @return Danh sách các đối tượng BanAnDTO.
     */
    @SuppressLint("Range")
    public List<BanAnDTO> LayTatCaBanAn() {
        List<BanAnDTO> banAnDTOList = new ArrayList<>();
        String truyvan = "SELECT * FROM " + CreateDatabase.TB_BANAN;
        Cursor cursor = database.rawQuery(truyvan, null);
        //mới thêm
        int colTinhTrang = cursor.getColumnIndex(CreateDatabase.TB_BANAN_TINHTRANG);
        try {
            if (cursor.moveToFirst()) {
                do {
                    BanAnDTO banAnDTO = new BanAnDTO();
                    banAnDTO.setMaBan(cursor.getInt(cursor.getColumnIndex(CreateDatabase.TB_BANAN_MABAN)));
                    banAnDTO.setTenBan(cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_BANAN_TENBAN)));
                    banAnDTOList.add(banAnDTO);
                    //mới thêm
                    banAnDTO.setTinhTrang(cursor.getString(colTinhTrang));
                } while (cursor.moveToNext());
            }
        } finally {
            // Đảm bảo cursor luôn được đóng, ngay cả khi có lỗi xảy ra
            if (cursor != null) {
                cursor.close();
            }
        }
        return banAnDTOList;
    }

    /**
     * Lấy tên của một bàn ăn dựa vào mã bàn.
     * @param maban Mã bàn cần lấy tên.
     * @return Tên bàn, hoặc chuỗi rỗng nếu không tìm thấy.
     */
    @SuppressLint("Range")
    public String LayTenBan(int maban) {
        String tenBan = "";
        String query = "SELECT " + CreateDatabase.TB_BANAN_TENBAN + " FROM " + CreateDatabase.TB_BANAN + " WHERE " + CreateDatabase.TB_BANAN_MABAN + " = ?";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, new String[]{String.valueOf(maban)});
            if (cursor.moveToFirst()) {
                tenBan = cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_BANAN_TENBAN));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tenBan;
    }


    /**
     * Cập nhật tên của một bàn ăn.
     * @param maban Mã bàn cần cập nhật.
     * @param tenban Tên mới cho bàn.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean CapNhatTenBan(int maban, String tenban) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_BANAN_TENBAN, tenban);

        // Sử dụng tham số hóa để tránh SQL Injection
        String whereClause = CreateDatabase.TB_BANAN_MABAN + " = ?";
        String[] whereArgs = {String.valueOf(maban)};

        int rowsAffected = database.update(CreateDatabase.TB_BANAN, contentValues, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    /**
     * Xóa một bàn ăn khỏi cơ sở dữ liệu.
     * @param maban Mã bàn cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean XoaBanAn(int maban) {
        // Sử dụng tham số hóa để tránh SQL Injection
        String whereClause = CreateDatabase.TB_BANAN_MABAN + " = ?";
        String[] whereArgs = {String.valueOf(maban)};

        int rowsAffected = database.delete(CreateDatabase.TB_BANAN, whereClause, whereArgs);
        // Trả về true nếu có ít nhất 1 dòng bị ảnh hưởng (bị xóa)
        return rowsAffected > 0;
    }

    // Các phương thức LayTinhTrangBan và CapNhatTinhTrangBan cũng nên được tối ưu tương tự
    // nhưng tạm thời tôi sẽ để lại theo code gốc của bạn.

    @SuppressLint("Range")
    public String LayTinhTrangBan(int maban){
        String tinhtrang = "";
        String truyvan = "SELECT " + CreateDatabase.TB_BANAN_TINHTRANG + " FROM " + CreateDatabase.TB_BANAN + " WHERE " + CreateDatabase.TB_BANAN_MABAN + " = ?";
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(truyvan, new String[]{String.valueOf(maban)});
            if (cursor.moveToFirst()) {
                tinhtrang = cursor.getString(cursor.getColumnIndex(CreateDatabase.TB_BANAN_TINHTRANG));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tinhtrang;
    }

    public boolean CapNhatTinhTrangBan(int maban, String tinhtrang){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CreateDatabase.TB_BANAN_TINHTRANG, tinhtrang);

        String whereClause = CreateDatabase.TB_BANAN_MABAN + " = ?";
        String[] whereArgs = {String.valueOf(maban)};

        int rowsAffected = database.update(CreateDatabase.TB_BANAN, contentValues, whereClause, whereArgs);
        return rowsAffected > 0;
    }
}

