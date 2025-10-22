
package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anhki.foodapp.Contants;
import com.example.anhki.foodapp.DTO.BanAnDTO;
import com.example.anhki.foodapp.R;

import java.util.List;

// Đổi sang kế thừa RecyclerView.Adapter và dùng ViewHolder bên trong nó
public class AdapterHienThiBanAn extends RecyclerView.Adapter<AdapterHienThiBanAn.ViewHolder> {

    // Interface giữ nguyên
    public interface BanAnClickListener {
        void onTableClick(int position); // Đổi tên cho rõ nghĩa (click vào cả thẻ CardView)
        void onGoiMonClick(int position);
        void onThanhToanClick(int position);
        void onSuaTenClick(int position); // Sửa tên giờ kích hoạt bằng Long Click
        void onXoaClick(int position);
    }

    private final Context context;
    private final List<BanAnDTO> banAnDTOList;
    private final int maquyen;
    private final BanAnClickListener listener;
    private int expandedPosition = -1; // Vị trí item đang hiển thị nút

    public AdapterHienThiBanAn(Context context, int layout, List<BanAnDTO> banAnDTOList, int maquyen, BanAnClickListener listener) {
        this.context = context;
        // layout không cần dùng nữa
        this.banAnDTOList = banAnDTOList;
        this.maquyen = maquyen;
        this.listener = listener;
    }

    // --- ViewHolder Class ---
    // Class ViewHolder phải extends RecyclerView.ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenBanAn;
        ImageView imBanAn;
        CardView cardBanAn;
        LinearLayout layoutButtons;
        ImageView imGoiMon, imThanhToan, imAnButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View ngay trong constructor của ViewHolder
            txtTenBanAn = itemView.findViewById(R.id.txtTenBanAn);
            imBanAn = itemView.findViewById(R.id.imBanAn);
            cardBanAn = itemView.findViewById(R.id.card_banan);
            layoutButtons = itemView.findViewById(R.id.layoutButtons);
            imGoiMon = itemView.findViewById(R.id.imGoiMon);
            imThanhToan = itemView.findViewById(R.id.imThanhToan);
            imAnButton = itemView.findViewById(R.id.imAnButton);
        }
    }

    // --- Các phương thức bắt buộc của RecyclerView.Adapter ---

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo View mới từ file layout XML
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_hienthibanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy dữ liệu tại vị trí 'position'
        BanAnDTO banAn = banAnDTOList.get(position);

        // Gán dữ liệu vào ViewHolder
        holder.txtTenBanAn.setText(banAn.getTenBan());

        // 1. HIỂN THỊ TRẠNG THÁI BẰNG MÀU SẮC VÀ ICON
        if ("true".equals(banAn.getTinhTrang())) {
            holder.cardBanAn.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorOccupied));
            holder.imBanAn.setImageResource(R.drawable.banantrue); // Icon bàn có khách
        } else {
            holder.cardBanAn.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorFree));
            holder.imBanAn.setImageResource(R.drawable.banan); // Icon bàn trống
        }

        // 2. HIỂN THỊ / ẨN CÁC NÚT BẤM
        boolean isExpanded = (position == expandedPosition);
        holder.layoutButtons.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // 3. PHÂN QUYỀN
        if (maquyen == Contants.QUYEN_QUANLY) {
            // Quản lý: Thấy nút Xóa, nhấn giữ itemView để sửa
            holder.imAnButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            // Gán Long Click cho toàn bộ item View
            holder.itemView.setOnLongClickListener(v -> {
                if(listener != null) {
                    listener.onSuaTenClick(holder.getAdapterPosition());
                }
                return true; // Đã xử lý long click
            });
        } else {
            // Nhân viên: Không thấy nút Xóa, không nhấn giữ để sửa
            holder.imAnButton.setVisibility(View.GONE);
            holder.itemView.setOnLongClickListener(null); // Vô hiệu hóa long click
        }

        // 4. GÁN SỰ KIỆN CLICK
        // Click vào hình cái bàn (hoặc cả CardView) để hiện/ẩn các nút
        holder.imBanAn.setOnClickListener(v -> {
            // holder.itemView.setOnClickListener(v -> { // Nếu muốn click cả CardView
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition == RecyclerView.NO_POSITION) return; // Kiểm tra an toàn

            int oldPosition = expandedPosition;
            expandedPosition = isExpanded ? -1 : clickedPosition;

            // Thông báo hiệu quả hơn cho RecyclerView
            if (oldPosition != -1) notifyItemChanged(oldPosition);
            if (expandedPosition != -1) notifyItemChanged(expandedPosition);
        });

        // Gán sự kiện cho các nút, dùng holder.getAdapterPosition()
        holder.imGoiMon.setOnClickListener(v -> {
            if (listener != null) listener.onGoiMonClick(holder.getAdapterPosition());
        });
        holder.imThanhToan.setOnClickListener(v -> {
            if (listener != null) listener.onThanhToanClick(holder.getAdapterPosition());
        });
        holder.imAnButton.setOnClickListener(v -> {
            if (listener != null) listener.onXoaClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng item trong danh sách
        return banAnDTOList.size();
    }
}