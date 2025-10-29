package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anhki.foodapp.DTO.BaoCaoDTO; // Dùng DTO mới
import com.example.anhki.foodapp.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterBaoCaoDoanhThu extends RecyclerView.Adapter<AdapterBaoCaoDoanhThu.ViewHolder> {

    private final Context context;
    private final List<BaoCaoDTO> hoaDonList;

    public AdapterBaoCaoDoanhThu(Context context, List<BaoCaoDTO> hoaDonList) {
        this.context = context;
        this.hoaDonList = hoaDonList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNgayThanhToan,txtGioThanhToan, txtTenBanThanhToan, txtTongTienHoaDon;
        TextView txtTenNhanVienHoaDon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNgayThanhToan = itemView.findViewById(R.id.txtNgayThanhToan);
            txtGioThanhToan = itemView.findViewById(R.id.txtGioThanhToan);
            txtTenBanThanhToan = itemView.findViewById(R.id.txtTenBanThanhToan);
            txtTenNhanVienHoaDon = itemView.findViewById(R.id.txtTenNhanVienHoaDon);
            txtTongTienHoaDon = itemView.findViewById(R.id.txtTongTienHoaDon);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_baocao_hoadon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaoCaoDTO hoaDon = hoaDonList.get(position);

        holder.txtNgayThanhToan.setText(hoaDon.getNgayGoi());
        holder.txtGioThanhToan.setText(hoaDon.getGioThanhToan());
        holder.txtTenBanThanhToan.setText(hoaDon.getTenBan());
        holder.txtTenNhanVienHoaDon.setText(hoaDon.getTenNhanVien());
        holder.txtTongTienHoaDon.setText(formatVND(hoaDon.getTongTien()));
    }

    @Override
    public int getItemCount() {
        return hoaDonList.size();
    }

    private String formatVND(long amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        return currencyFormatter.format(amount);
    }
}
