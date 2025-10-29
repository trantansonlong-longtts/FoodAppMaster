package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.firestore.FirebaseFirestore; // Thêm import
import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.R;
import java.util.List;

public class AdapterHienThiNhanVien extends BaseAdapter {
    private static final String TAG = "AdapterHienThiNV";
    private final Context context;
    private final int layout;
    private final List<NhanVienDTO> nhanVienList;
    private final FirebaseFirestore db; // Biến để truy cập Firestore


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
        ViewHolderNhanVien viewHolder; // Dùng tên ViewHolder đúng
        if (convertView == null){
            viewHolder = new ViewHolderNhanVien();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
            // ... (ánh xạ view giữ nguyên)
            viewHolder.imHinhNhanVien = convertView.findViewById(R.id.imHinhNhanVien);
            viewHolder.txtTenNhanVien = convertView.findViewById(R.id.txtTenNhanVien);
            viewHolder.txtCMND = convertView.findViewById(R.id.txtCMND);
            viewHolder.txtQuyenNhanVien= convertView.findViewById(R.id.txtQuyenNhanVien);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderNhanVien) convertView.getTag();
        }

        NhanVienDTO nhanVien = nhanVienList.get(position);
        viewHolder.txtTenNhanVien.setText(nhanVien.getTENDANGNHAP());
        viewHolder.txtCMND.setText("CCCD: " + nhanVien.getCMND());

        // LẤY TÊN QUYỀN TỪ FIRESTORE (Bất đồng bộ)
        int maQuyen = nhanVien.getMAQUYEN();
        viewHolder.txtQuyenNhanVien.setText("Đang tải..."); // Hiển thị tạm
        db.collection("quyen").document(String.valueOf(maQuyen))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tenQuyen = documentSnapshot.getString("tenQuyen");
                        viewHolder.txtQuyenNhanVien.setText(tenQuyen);
                    } else {
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