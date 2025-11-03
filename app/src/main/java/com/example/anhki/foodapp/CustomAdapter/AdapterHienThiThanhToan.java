package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anhki.foodapp.DTO.ChiTietGoiMonDTO; // Sử dụng DTO chi tiết
import com.example.anhki.foodapp.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// Kế thừa RecyclerView.Adapter
public class AdapterHienThiThanhToan extends RecyclerView.Adapter<AdapterHienThiThanhToan.ViewHolder> {

    private final Context context;
    private final List<ChiTietGoiMonDTO> chiTietList; // Danh sách chi tiết món ăn
    private final MonAnClickListener listener;
    public interface MonAnClickListener {
        void onItemClick(int position);//sửa
        void onItemLongClick(int position);//xóa
    }

    // Constructor nhận Context và List
    public AdapterHienThiThanhToan(Context context, List<ChiTietGoiMonDTO> chiTietList, MonAnClickListener listener) {
        this.context = context;
        this.chiTietList = chiTietList;
        this.listener = listener;
    }

    // Constructor cũ (giữ lại để tương thích nếu cần, nhưng không nên dùng)
    @Deprecated
    public AdapterHienThiThanhToan(Context context, int layout, List<ChiTietGoiMonDTO> chiTietList) {
        this(context, chiTietList, null); // Gọi constructor mới với listener là null
    }


    // ViewHolder class để giữ các View của một item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenMonAn, txtSoLuong, txtGiaTien;

        public ViewHolder(@NonNull View itemView, MonAnClickListener listener) { // Nhận listener
            super(itemView);
            txtTenMonAn = itemView.findViewById(R.id.txtTenMonChiTiet);
            txtSoLuong = itemView.findViewById(R.id.txtSoLuongChiTiet);
            txtGiaTien = itemView.findViewById(R.id.txtGiaTienChiTiet);

            // Gán sự kiện cho toàn bộ item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(position);
                        return true; // Đã xử lý long click
                    }
                }
                return false;
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chitiet_goimon, parent, false);
        return new ViewHolder(view, listener); // Truyền listener vào ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChiTietGoiMonDTO chiTiet = chiTietList.get(position);
        holder.txtTenMonAn.setText(chiTiet.getTenMonAn());
        holder.txtSoLuong.setText("x " + chiTiet.getSoLuong());

        // Hiển thị tổng tiền cho dòng này (Giá * Số lượng)
        long tongTienMon = chiTiet.getGiaTien() * chiTiet.getSoLuong();
        holder.txtGiaTien.setText(formatVND(tongTienMon));
    }

    @Override
    public int getItemCount() {
        return chiTietList.size();
    }

    private String formatVND(long amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        return currencyFormatter.format(amount);
    }
}