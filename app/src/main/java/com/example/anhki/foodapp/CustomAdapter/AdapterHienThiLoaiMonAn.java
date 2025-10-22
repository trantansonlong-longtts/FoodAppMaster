//package com.example.anhki.foodapp.CustomAdapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;
//import com.example.anhki.foodapp.R;
//
//import java.util.List;
//
//public class AdapterHienThiLoaiMonAn extends BaseAdapter{
//    private final Context context;
//    private final int layout;
//    private final List<LoaiMonAnDTO> loaiMonAnDTOList;
//    private ViewHolderLoaiMonAn viewHolderLoaiMonAn;
//
//    public AdapterHienThiLoaiMonAn(Context context, int layout, List<LoaiMonAnDTO> loaiMonAnDTOList){
//        this.context = context;
//        this.layout = layout;
//        this.loaiMonAnDTOList = loaiMonAnDTOList;
//    }
//
//    @Override
//    public int getCount() {
//        return loaiMonAnDTOList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return loaiMonAnDTOList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return loaiMonAnDTOList.get(position).getMaLoai();
//    }
//
//    public static class ViewHolderLoaiMonAn{
//        TextView txtTenLoai;
//    }
//
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (view == null){
//            viewHolderLoaiMonAn = new ViewHolderLoaiMonAn();
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.custom_layout_spinloaithucdon, parent, false);
//
//            viewHolderLoaiMonAn.txtTenLoai = (TextView) view.findViewById(R.id.txtTenLoai);
//
//            view.setTag(viewHolderLoaiMonAn);
//        } else
//            viewHolderLoaiMonAn = (ViewHolderLoaiMonAn) view.getTag();
//
//        LoaiMonAnDTO loaiMonAnDTO = loaiMonAnDTOList.get(position);
//        viewHolderLoaiMonAn.txtTenLoai.setText(loaiMonAnDTO.getTenLoai());
//        viewHolderLoaiMonAn.txtTenLoai.setTag(loaiMonAnDTO.getMaLoai());
//
//        return view;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (view == null){
//            viewHolderLoaiMonAn = new ViewHolderLoaiMonAn();
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = inflater.inflate(R.layout.custom_layout_spinloaithucdon, parent, false);
//
//            viewHolderLoaiMonAn.txtTenLoai = view.findViewById(R.id.txtTenLoai);
//
//            view.setTag(viewHolderLoaiMonAn);
//        } else
//            viewHolderLoaiMonAn = (ViewHolderLoaiMonAn) view.getTag();
//
//        LoaiMonAnDTO loaiMonAnDTO = loaiMonAnDTOList.get(position);
//        viewHolderLoaiMonAn.txtTenLoai.setText(loaiMonAnDTO.getTenLoai());
//        viewHolderLoaiMonAn.txtTenLoai.setTag(loaiMonAnDTO.getMaLoai());
//
//        return view;
//    }
//}
//package com.example.anhki.foodapp.CustomAdapter;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;
//import com.example.anhki.foodapp.R;
//
//import java.util.List;
//
//public class AdapterHienThiLoaiMonAn extends BaseAdapter {
//    private final Context context;
//    private final int layout;
//    private final List<LoaiMonAnDTO> loaiMonAnDTOList;
//
//    public AdapterHienThiLoaiMonAn(Context context, int layout, List<LoaiMonAnDTO> loaiMonAnDTOList) {
//        this.context = context;
//        this.layout = layout;
//        this.loaiMonAnDTOList = loaiMonAnDTOList;
//    }
//
//    @Override
//    public int getCount() {
//        return loaiMonAnDTOList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return loaiMonAnDTOList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return loaiMonAnDTOList.get(position).getMaLoai();
//    }
//
//    private static class ViewHolder {
//        ImageView imHinhLoai;
//        TextView txtTenLoai;
//    }
//
//    // Phương thức này dùng cho cả GridView và hiển thị mục đã chọn trên Spinner
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        return createView(position, convertView, parent, R.layout.custom_layout_hienloaimonan);
//    }
//
//    // Phương thức này chỉ dùng cho danh sách thả xuống của Spinner
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        // Có thể dùng một layout khác cho danh sách thả xuống nếu muốn
//        return createView(position, convertView, parent, R.layout.custom_layout_spinloaithucdon);
//    }
//
//    // Hàm private dùng chung để tránh lặp code
//    private View createView(int position, View convertView, ViewGroup parent, int layoutResource) {
//        ViewHolder holder;
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(layoutResource, parent, false);
//
//            holder = new ViewHolder();
//            holder.imHinhLoai = convertView.findViewById(R.id.imHienThiMonAn); // Giả sử ID giống nhau
//            holder.txtTenLoai = convertView.findViewById(R.id.txtTenLoaiThucDon); // Giả sử ID giống nhau
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        LoaiMonAnDTO loai = loaiMonAnDTOList.get(position);
//
//        if (holder.txtTenLoai != null) {
//            holder.txtTenLoai.setText(loai.getTenLoai());
//        }
//
//        if (holder.imHinhLoai != null) {
//            byte[] hinhAnh = loai.getHinhAnh();
//            if (hinhAnh != null && hinhAnh.length > 0) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
//                holder.imHinhLoai.setImageBitmap(bitmap);
//            } else {
//                holder.imHinhLoai.setImageResource(R.drawable.backgroundheader1); // Ảnh mặc định
//            }
//        }
//
//        return convertView;
//    }
//}
package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;
import com.example.anhki.foodapp.R;

import java.util.List;

public class AdapterHienThiLoaiMonAn extends BaseAdapter {
    private final Context context;
    private final int layout; // Layout ID (vd: R.layout.custom_layout_hienloaimonan)
    private final List<LoaiMonAnDTO> loaiMonAnDTOList;

    // Constructor đơn giản hơn, chỉ cần 3 tham số
    public AdapterHienThiLoaiMonAn(Context context, int layout, List<LoaiMonAnDTO> loaiMonAnDTOList) {
        this.context = context;
        this.layout = layout;
        this.loaiMonAnDTOList = loaiMonAnDTOList;
    }

    @Override
    public int getCount() {
        return loaiMonAnDTOList.size();
    }

    @Override
    public Object getItem(int position) {
        return loaiMonAnDTOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // Trả về 0 hoặc một ID ổn định khác nếu maloai không còn dùng
        return 0;
    }

    private static class ViewHolder {
        ImageView imHinhLoai;
        TextView txtTenLoai;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);

            holder = new ViewHolder();
            // Đảm bảo ID trong layout của bạn khớp với các ID này
            holder.imHinhLoai = convertView.findViewById(R.id.imHienThiMonAn);
            holder.txtTenLoai = convertView.findViewById(R.id.txtTenLoaiThucDon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LoaiMonAnDTO loai = loaiMonAnDTOList.get(position);

        // Hiển thị tên
        if (holder.txtTenLoai != null) {
            holder.txtTenLoai.setText(loai.getTenLoai());
        }

        // Hiển thị hình ảnh
        if (holder.imHinhLoai != null) {
            String hinhAnhBase64 = loai.getHinhAnh(); // Giả định DTO trả về byte[]
            // KIỂM TRA VÀ GIẢI MÃ
            if (hinhAnhBase64 != null && !hinhAnhBase64.isEmpty()) {
                try {
                    // Chuyển String Base64 thành byte[]
                    byte[] hinhAnhBytes = Base64.decode(hinhAnhBase64, Base64.DEFAULT);
                    // Chuyển byte[] thành Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnhBytes, 0, hinhAnhBytes.length);
                    holder.imHinhLoai.setImageBitmap(bitmap);
                } catch (IllegalArgumentException | OutOfMemoryError e) {
                    Log.e("AdapterLoaiMonAn", "Lỗi giải mã ảnh Base64", e);
                    holder.imHinhLoai.setImageResource(R.drawable.logodangnhap); // Ảnh lỗi
                }
            } else {
                holder.imHinhLoai.setImageResource(R.drawable.logodangnhap); // Ảnh mặc định
            }
        }

        return convertView;
    }
}