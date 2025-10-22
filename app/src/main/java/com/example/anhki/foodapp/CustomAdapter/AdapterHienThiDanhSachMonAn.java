//package com.example.anhki.foodapp.CustomAdapter;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.anhki.foodapp.DTO.MonAnDTO;
//import com.example.anhki.foodapp.R;
//
//import java.util.List;
//
//public class AdapterHienThiDanhSachMonAn extends BaseAdapter{
//    private final Context context;
//    private final int layout;
//    private final List<MonAnDTO> monAnDTOList;
//    private ViewHolderHienThiDanhSachMonAn viewHolderHienThiDanhSachMonAn;
//
//    public AdapterHienThiDanhSachMonAn(Context context, int layout, List<MonAnDTO> monAnDTOList){
//        this.context = context;
//        this.layout = layout;
//        this.monAnDTOList = monAnDTOList;
//    }
//
//    @Override
//    public int getCount() {
//        return monAnDTOList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return monAnDTOList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return monAnDTOList.get(position).getMaMonAn();
//    }
//
//    public static class ViewHolderHienThiDanhSachMonAn{
//        ImageView imHinhMonAn;
//        TextView txtTenMonAn, txtGiaTien;
//    }
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        if (view == null){
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            viewHolderHienThiDanhSachMonAn = new ViewHolderHienThiDanhSachMonAn();
//            view = inflater.inflate(layout, parent, false);
//
//            viewHolderHienThiDanhSachMonAn.imHinhMonAn = view.findViewById(R.id.imHienThiDSMonAn);
//            viewHolderHienThiDanhSachMonAn.txtTenMonAn = view.findViewById(R.id.txtTenDSMonAn);
//            viewHolderHienThiDanhSachMonAn.txtGiaTien = view.findViewById(R.id.txtGiaTienDSMonAn);
//
//            view.setTag(viewHolderHienThiDanhSachMonAn);
//        }else
//            viewHolderHienThiDanhSachMonAn = (ViewHolderHienThiDanhSachMonAn) view.getTag();
//
//        MonAnDTO monAnDTO = monAnDTOList.get(position);
//        String hinhanh = monAnDTO.getHinhAnh();
//
//        if(hinhanh == null || hinhanh.equals(""))
//            viewHolderHienThiDanhSachMonAn.imHinhMonAn.setImageResource(R.drawable.backgroundheader1);
//        else{
//            Uri uri = Uri.parse(hinhanh);
//            viewHolderHienThiDanhSachMonAn.imHinhMonAn.setImageURI(uri);
//        }
//
//        viewHolderHienThiDanhSachMonAn.txtTenMonAn.setText(monAnDTO.getTenMonAn());
//        viewHolderHienThiDanhSachMonAn.txtGiaTien.setText(context.getResources().getString(R.string.gia) + ": " + monAnDTO.getGiaTien());
//
//        return view;
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

import com.example.anhki.foodapp.DTO.MonAnDTO;
import com.example.anhki.foodapp.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterHienThiDanhSachMonAn extends BaseAdapter {
    private final Context context;
    private final int layout;
    private final List<MonAnDTO> monAnDTOList;

    public AdapterHienThiDanhSachMonAn(Context context, int layout, List<MonAnDTO> monAnDTOList) {
        this.context = context;
        this.layout = layout;
        this.monAnDTOList = monAnDTOList;
    }

    @Override
    public int getCount() {
        return monAnDTOList.size();
    }

    @Override
    public Object getItem(int position) {
        return monAnDTOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return monAnDTOList.get(position).getMaMonAn();
    }

    private static class ViewHolderHienThiDanhSachMonAn {
        ImageView imHinhMonAn;
        TextView txtTenMonAn, txtGiaTien;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Sửa lỗi ViewHolder toàn cục
        ViewHolderHienThiDanhSachMonAn viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolderHienThiDanhSachMonAn();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);

            viewHolder.imHinhMonAn = convertView.findViewById(R.id.imHienThiDSMonAn);
            viewHolder.txtTenMonAn = convertView.findViewById(R.id.txtTenDSMonAn);
            viewHolder.txtGiaTien = convertView.findViewById(R.id.txtGiaTienDSMonAn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderHienThiDanhSachMonAn) convertView.getTag();
        }

        MonAnDTO monAnDTO = monAnDTOList.get(position);

        // SỬA LỖI HIỂN THỊ HÌNH ẢNH
        String hinhAnhBase64 = monAnDTO.getHinhAnh();
        // KIỂM TRA VÀ GIẢI MÃ
        if (hinhAnhBase64 != null && !hinhAnhBase64.isEmpty()) {
            try {
                // Chuyển String Base64 thành byte[]
                byte[] hinhAnhBytes = Base64.decode(hinhAnhBase64, Base64.DEFAULT);
                // Chuyển byte[] thành Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnhBytes, 0, hinhAnhBytes.length);
                viewHolder.imHinhMonAn.setImageBitmap(bitmap);
            } catch (IllegalArgumentException | OutOfMemoryError e) {
                Log.e("AdapterLoaiMonAn", "Lỗi giải mã ảnh Base64", e);
                viewHolder.imHinhMonAn.setImageResource(R.drawable.logodangnhap); // Ảnh lỗi
            }
        }
         else {
        viewHolder.imHinhMonAn.setImageResource(R.drawable.logodangnhap); // Ảnh mặc định
    }

        viewHolder.txtTenMonAn.setText(monAnDTO.getTenMonAn());

        // Định dạng lại giá tiền
        String giaTienFormatted = formatVND(monAnDTO.getGiaTien());
        viewHolder.txtGiaTien.setText(giaTienFormatted);

        return convertView;
    }

    private String formatVND(int amount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");
        return formatter.format(amount) + " VNĐ";
    }
}