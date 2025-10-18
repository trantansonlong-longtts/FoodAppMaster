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
//import com.example.anhki.foodapp.DTO.NhanVienDTO;
//import com.example.anhki.foodapp.R;
//
//import java.util.List;
//
//public class AdapterHienThiNhanVien extends BaseAdapter{
//    private final Context context;
//    private final int layout;
//    private final List<NhanVienDTO> nhanVienDTOList;
//
//    private ViewHolderNhanVien viewHolderNhanVien;
//
//    public AdapterHienThiNhanVien(Context context, int layout, List<NhanVienDTO> nhanVienDTOList){
//        this.context = context;
//        this.layout = layout;
//        this.nhanVienDTOList = nhanVienDTOList;
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
//    public static class ViewHolderNhanVien{
//        ImageView imHinhNhanVien;
//        TextView txtTenNhanVien, txtCMND, txtQuyenNhanVien;
//    }
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (view == null){
//            viewHolderNhanVien = new ViewHolderNhanVien();
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(layout, parent, false);
//
//            viewHolderNhanVien.imHinhNhanVien = view.findViewById(R.id.imHinhNhanVien);
//            viewHolderNhanVien.txtTenNhanVien = view.findViewById(R.id.txtTenNhanVien);
//            viewHolderNhanVien.txtCMND = view.findViewById(R.id.txtCMND);
//            viewHolderNhanVien.txtQuyenNhanVien= view.findViewById(R.id.txtQuyenNhanVien);
//            view.setTag(viewHolderNhanVien);
//        } else {
//            viewHolderNhanVien = (ViewHolderNhanVien) view.getTag();
//        }
//
//        NhanVienDTO nhanVienDTO = nhanVienDTOList.get(position);
//
//        // Chuyển mọi giá trị sang String để tránh Resources$NotFoundException
//        viewHolderNhanVien.txtTenNhanVien.setText(nhanVienDTO.getTENDANGNHAP() != null ? nhanVienDTO.getTENDANGNHAP() : "");
//        viewHolderNhanVien.txtCMND.setText(String.valueOf(nhanVienDTO.getCMND()));
//        viewHolderNhanVien.txtQuyenNhanVien.setText(String.valueOf(nhanVienDTO.getMAQUYEN()));
//
//        return view;
//    }
//}
package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anhki.foodapp.DAO.QuyenDAO; // Import QuyenDAO
import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.R;
import com.example.anhki.foodapp.Contants;

import java.util.List;

public class AdapterHienThiNhanVien extends BaseAdapter {
    private final Context context;
    private final int layout;
    private final List<NhanVienDTO> nhanVienDTOList;
    private final QuyenDAO quyenDAO; // CẢI TIẾN: Dùng lại QuyenDAO từ Fragment

    // CẢI TIẾN: Sửa lại Constructor để nhận vào QuyenDAO
    public AdapterHienThiNhanVien(Context context, int layout, List<NhanVienDTO> nhanVienDTOList, QuyenDAO quyenDAO) {
        this.context = context;
        this.layout = layout;
        this.nhanVienDTOList = nhanVienDTOList;
        this.quyenDAO = quyenDAO; // Nhận đối tượng DAO đã được khởi tạo
    }

    @Override
    public int getCount() {
        return nhanVienDTOList.size();
    }

    @Override
    public Object getItem(int position) {
        return nhanVienDTOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return nhanVienDTOList.get(position).getMANV();
    }

    // ViewHolder class không thay đổi
    private static class ViewHolderNhanVien {
        ImageView imHinhNhanVien;
        TextView txtTenNhanVien, txtCMND, txtQuyenNhanVien;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // SỬA LỖI NGHIÊM TRỌNG: ViewHolder phải là biến cục bộ trong getView()
        ViewHolderNhanVien viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolderNhanVien();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);

            viewHolder.imHinhNhanVien = convertView.findViewById(R.id.imHinhNhanVien);
            viewHolder.txtTenNhanVien = convertView.findViewById(R.id.txtTenNhanVien);
            viewHolder.txtCMND = convertView.findViewById(R.id.txtCMND);
            viewHolder.txtQuyenNhanVien = convertView.findViewById(R.id.txtQuyenNhanVien);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderNhanVien) convertView.getTag();
        }

        NhanVienDTO nhanVienDTO = nhanVienDTOList.get(position);

        // Hiển thị Tên đăng nhập (vì DTO không có Họ Tên)
        viewHolder.txtTenNhanVien.setText(nhanVienDTO.getTENDANGNHAP());
        // Hiển thị CMND (giả sử CMND đã là String trong DTO)
        viewHolder.txtCMND.setText("CCCD: " + nhanVienDTO.getCMND());

        // CẢI TIẾN CHỨC NĂNG: Hiển thị tên quyền thay vì mã quyền
        int maQuyen = nhanVienDTO.getMAQUYEN();
        String tenQuyen = quyenDAO.LayTenQuyenTheoMa(maQuyen);
        viewHolder.txtQuyenNhanVien.setText(tenQuyen);

        return convertView;
    }
}