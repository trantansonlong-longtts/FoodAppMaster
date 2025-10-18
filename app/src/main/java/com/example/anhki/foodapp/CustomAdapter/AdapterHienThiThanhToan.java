package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anhki.foodapp.DTO.ThanhToanDTO;
import com.example.anhki.foodapp.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdapterHienThiThanhToan extends BaseAdapter {
    private final Context context;
    private final int layout;
    private final List<ThanhToanDTO> thanhToanDTOList;

    public AdapterHienThiThanhToan(Context context, int layout, List<ThanhToanDTO> thanhToanDTOList) {
        this.context = context;
        this.layout = layout;
        this.thanhToanDTOList = thanhToanDTOList;
    }

    @Override
    public int getCount() {
        return thanhToanDTOList.size();
    }

    @Override
    public Object getItem(int position) {
        return thanhToanDTOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0; // Hoặc một ID duy nhất nếu có
    }

    private static class ViewHolderThanhToan {
        TextView txtTenMonAn, txtSoLuong, txtGiaTien;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // SỬA LỖI: ViewHolder phải là biến cục bộ
        ViewHolderThanhToan viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolderThanhToan();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);

            viewHolder.txtTenMonAn = convertView.findViewById(R.id.txtTenMonAnThanToan);
            viewHolder.txtGiaTien = convertView.findViewById(R.id.txtGiaTienThanhToan);
            viewHolder.txtSoLuong = convertView.findViewById(R.id.txtSoLuongThanhToan);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderThanhToan) convertView.getTag();
        }

        ThanhToanDTO thanhToanDTO = thanhToanDTOList.get(position);

        viewHolder.txtTenMonAn.setText(thanhToanDTO.getTenMonAn());
        viewHolder.txtSoLuong.setText(String.valueOf(thanhToanDTO.getSoLuong()));

        // GỌI HÀM ĐỊNH DẠNG TIỀN TỆ
        String giaTienFormatted = formatVND(thanhToanDTO.getGiatien());
        viewHolder.txtGiaTien.setText(giaTienFormatted);

        return convertView;
    }

    /**
     * Hàm dùng để định dạng số nguyên thành chuỗi tiền tệ VNĐ.
     * Ví dụ: 50000 -> "50,000 VNĐ"
     * @param amount Số tiền cần định dạng.
     * @return Chuỗi đã được định dạng.
     */
    private String formatVND(int amount) {
        // Tạo một đối tượng DecimalFormat với pattern "#,###" để có dấu phẩy ngăn cách
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");

        // Định dạng số và thêm đơn vị " VNĐ" vào cuối
        return formatter.format(amount) + " VNĐ";
    }
}