//package com.example.anhki.foodapp.CustomAdapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.example.anhki.foodapp.DTO.ThanhToanDTO;
//import com.example.anhki.foodapp.R;
//
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.util.List;
//import java.util.Locale;
//
//public class AdapterHienThiThanhToan extends BaseAdapter {
//    private final Context context;
//    private final int layout;
//    private final List<ThanhToanDTO> thanhToanDTOList;
//
//    public AdapterHienThiThanhToan(Context context, int layout, List<ThanhToanDTO> thanhToanDTOList) {
//        this.context = context;
//        this.layout = layout;
//        this.thanhToanDTOList = thanhToanDTOList;
//    }
//
//    @Override
//    public int getCount() {
//        return thanhToanDTOList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return thanhToanDTOList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0; // Hoặc một ID duy nhất nếu có
//    }
//
//    private static class ViewHolderThanhToan {
//        TextView txtTenMonAn, txtSoLuong, txtGiaTien;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // SỬA LỖI: ViewHolder phải là biến cục bộ
//        ViewHolderThanhToan viewHolder;
//
//        if (convertView == null) {
//            viewHolder = new ViewHolderThanhToan();
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(layout, parent, false);
//
//            viewHolder.txtTenMonAn = convertView.findViewById(R.id.txtTenMonAnThanToan);
//            viewHolder.txtGiaTien = convertView.findViewById(R.id.txtGiaTienThanhToan);
//            viewHolder.txtSoLuong = convertView.findViewById(R.id.txtSoLuongThanhToan);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolderThanhToan) convertView.getTag();
//        }
//
//        ThanhToanDTO thanhToanDTO = thanhToanDTOList.get(position);
//
//        viewHolder.txtTenMonAn.setText(thanhToanDTO.getTenMonAn());
//        viewHolder.txtSoLuong.setText(String.valueOf(thanhToanDTO.getSoLuong()));
//
//        // GỌI HÀM ĐỊNH DẠNG TIỀN TỆ
//        String giaTienFormatted = formatVND(thanhToanDTO.getGiatien());
//        viewHolder.txtGiaTien.setText(giaTienFormatted);
//
//        return convertView;
//    }
//
//    /**
//     * Hàm dùng để định dạng số nguyên thành chuỗi tiền tệ VNĐ.
//     * Ví dụ: 50000 -> "50,000 VNĐ"
//     * @param amount Số tiền cần định dạng.
//     * @return Chuỗi đã được định dạng.
//     */
//    private String formatVND(int amount) {
//        // Tạo một đối tượng DecimalFormat với pattern "#,###" để có dấu phẩy ngăn cách
//        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//        formatter.applyPattern("#,###");
//
//        // Định dạng số và thêm đơn vị " VNĐ" vào cuối
//        return formatter.format(amount) + " VNĐ";
//    }
//}
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

    // Constructor nhận Context và List
    public AdapterHienThiThanhToan(Context context, List<ChiTietGoiMonDTO> chiTietList) {
        this.context = context;
        this.chiTietList = chiTietList;
    }

    // Constructor cũ (giữ lại để tương thích nếu cần, nhưng không nên dùng)
    @Deprecated
    public AdapterHienThiThanhToan(Context context, int layout, List<ChiTietGoiMonDTO> chiTietList) {
        this(context, chiTietList); // Gọi constructor mới
    }


    // ViewHolder class để giữ các View của một item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenMonAn, txtSoLuong, txtGiaTien;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các TextView từ layout item_chitiet_goimon.xml
            txtTenMonAn = itemView.findViewById(R.id.txtTenMonChiTiet);
            txtSoLuong = itemView.findViewById(R.id.txtSoLuongChiTiet);
            txtGiaTien = itemView.findViewById(R.id.txtGiaTienChiTiet);
        }
    }

    // Tạo ViewHolder mới (được gọi bởi layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_chitiet_goimon.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_chitiet_goimon, parent, false);
        return new ViewHolder(view);
    }

    // Gán dữ liệu vào ViewHolder (được gọi bởi layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy dữ liệu tại vị trí hiện tại
        ChiTietGoiMonDTO chiTiet = chiTietList.get(position);

        // Gán dữ liệu vào các TextView
        holder.txtTenMonAn.setText(chiTiet.getTenMonAn());
        holder.txtSoLuong.setText("x " + chiTiet.getSoLuong()); // Hiển thị số lượng

        // Định dạng và hiển thị giá tiền
        String giaTienFormatted = formatVND(chiTiet.getGiaTien());
        holder.txtGiaTien.setText(giaTienFormatted);
    }

    // Trả về tổng số item trong danh sách
    @Override
    public int getItemCount() {
        return chiTietList.size();
    }

    // Hàm định dạng tiền tệ VND
    private String formatVND(long amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        // Bỏ ký hiệu 'đ' nếu không muốn:
        // ((DecimalFormat)currencyFormatter).applyPattern("#,###"); return currencyFormatter.format(amount) + " VNĐ";
        return currencyFormatter.format(amount); // Trả về dạng "123.456 ₫"
    }
}