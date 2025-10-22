
//package com.example.anhki.foodapp.CustomAdapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.anhki.foodapp.DAO.QuyenDAO;
//import com.example.anhki.foodapp.DTO.NhanVienDTO;
//import com.example.anhki.foodapp.R;
//
//import java.util.List;
//
//public class AdapterHienThiNhanVien extends BaseAdapter {
//    private final Context context;
//    private final int layout;
//    private final List<NhanVienDTO> nhanVienDTOList;
//    private final QuyenDAO quyenDAO; // CẢI TIẾN: Dùng lại QuyenDAO từ Fragment
//
//    // CẢI TIẾN: Sửa lại Constructor để nhận vào QuyenDAO
//    public AdapterHienThiNhanVien(Context context, int layout, List<NhanVienDTO> nhanVienDTOList, QuyenDAO quyenDAO) {
//        this.context = context;
//        this.layout = layout;
//        this.nhanVienDTOList = nhanVienDTOList;
//        this.quyenDAO = quyenDAO; // Nhận đối tượng DAO đã được khởi tạo
//    }
//
//    @Override
//    public int getCount() {
//        return nhanVienDTOList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return nhanVienDTOList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return nhanVienDTOList.get(position).getMANV();
//    }
//
//    // ViewHolder class không thay đổi
//    private static class ViewHolderNhanVien {
//        ImageView imHinhNhanVien;
//        TextView txtTenNhanVien, txtCMND, txtQuyenNhanVien;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // SỬA LỖI NGHIÊM TRỌNG: ViewHolder phải là biến cục bộ trong getView()
//        ViewHolderNhanVien viewHolder;
//
//        if (convertView == null) {
//            viewHolder = new ViewHolderNhanVien();
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(layout, parent, false);
//
//            viewHolder.imHinhNhanVien = convertView.findViewById(R.id.imHinhNhanVien);
//            viewHolder.txtTenNhanVien = convertView.findViewById(R.id.txtTenNhanVien);
//            viewHolder.txtCMND = convertView.findViewById(R.id.txtCMND);
//            viewHolder.txtQuyenNhanVien = convertView.findViewById(R.id.txtQuyenNhanVien);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolderNhanVien) convertView.getTag();
//        }
//
//        NhanVienDTO nhanVienDTO = nhanVienDTOList.get(position);
//
//        // Hiển thị Tên đăng nhập (vì DTO không có Họ Tên)
//        viewHolder.txtTenNhanVien.setText(nhanVienDTO.getTENDANGNHAP());
//        // Hiển thị CMND (giả sử CMND đã là String trong DTO)
//
//        viewHolder.txtCMND.setText(nhanVienDTO.getCMND());
//
//        // CẢI TIẾN CHỨC NĂNG: Hiển thị tên quyền thay vì mã quyền
//        int maQuyen = nhanVienDTO.getMAQUYEN();
//        String tenQuyen = quyenDAO.LayTenQuyenTheoMa(maQuyen);
//        viewHolder.txtQuyenNhanVien.setText(tenQuyen);
//
//        return convertView;
//    }
//}

package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// Firebase Imports
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.R;

import java.util.List;

public class AdapterHienThiNhanVien extends BaseAdapter {
    private static final String TAG = "AdapterHienThiNV";
    private final Context context;
    private final int layout;
    private final List<NhanVienDTO> nhanVienList;
    private final FirebaseFirestore db; // Biến để truy cập Firestore

    // Sửa Constructor: nhận FirebaseFirestore, bỏ QuyenDAO
    public AdapterHienThiNhanVien(Context context, int layout, List<NhanVienDTO> nhanVienList, FirebaseFirestore db) {
        this.context = context;
        this.layout = layout;
        this.nhanVienList = nhanVienList;
        this.db = db;
    }

    @Override
    public int getCount() { return nhanVienList.size(); }
    @Override
    public Object getItem(int position) { return nhanVienList.get(position); }
    @Override
    public long getItemId(int position) { return position; } // Dùng position làm ID tạm thời

    private static class ViewHolderNhanVien {
        ImageView imHinhNhanVien;
        TextView txtTenNhanVien, txtCMND, txtQuyenNhanVien;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderNhanVien viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolderNhanVien();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
            // ... (ánh xạ view giữ nguyên)
            viewHolder.imHinhNhanVien = convertView.findViewById(R.id.imHinhNhanVien);
            viewHolder.txtTenNhanVien = convertView.findViewById(R.id.txtTenNhanVien);
            viewHolder.txtCMND = convertView.findViewById(R.id.txtCMND);
            viewHolder.txtQuyenNhanVien = convertView.findViewById(R.id.txtQuyenNhanVien);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderNhanVien) convertView.getTag();
        }

        NhanVienDTO nhanVien = nhanVienList.get(position);

        viewHolder.txtTenNhanVien.setText(nhanVien.getTENDANGNHAP());
        viewHolder.txtCMND.setText("CCCD: " + nhanVien.getCMND());

        // LẤY TÊN QUYỀN TỪ FIRESTORE (Bất đồng bộ)
        int maQuyen = nhanVien.getMAQUYEN();
        viewHolder.txtQuyenNhanVien.setText("Đang tải..."); // Hiển thị tạm thời
        db.collection("quyen").document(String.valueOf(maQuyen))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tenQuyen = documentSnapshot.getString("tenQuyen");
                        viewHolder.txtQuyenNhanVien.setText(tenQuyen);
                    } else {
                        Log.w(TAG, "Không tìm thấy quyền với mã: " + maQuyen);
                        viewHolder.txtQuyenNhanVien.setText("Không rõ");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi lấy tên quyền: " + maQuyen, e);
                    viewHolder.txtQuyenNhanVien.setText("Lỗi");
                });


        return convertView;
    }
}