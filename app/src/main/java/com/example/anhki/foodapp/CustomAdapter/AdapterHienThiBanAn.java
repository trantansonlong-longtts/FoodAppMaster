
package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
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

public class AdapterHienThiBanAn extends RecyclerView.Adapter<AdapterHienThiBanAn.ViewHolder> {

    public interface BanAnClickListener {
        void onTableClick(int position);
        void onGoiMonClick(int position);
        void onThanhToanClick(int position);
        void onSuaTenClick(int position);
        void onXoaClick(int position);
    }

    private final Context context;
    private final List<BanAnDTO> banAnDTOList;
    private final int maquyen;
    private final BanAnClickListener listener;
    private int expandedPosition = -1;

    public AdapterHienThiBanAn(Context context, int layout, List<BanAnDTO> banAnDTOList, int maquyen, BanAnClickListener listener) {
        this.context = context;
        // layout không cần dùng nữa nhưng giữ lại cho constructor khớp
        this.banAnDTOList = banAnDTOList;
        this.maquyen = maquyen;
        this.listener = listener;
    }

    // 1. ViewHolder giờ sẽ extends RecyclerView.ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenBanAn;
        ImageView imBanAn;
        CardView cardBanAn;
        LinearLayout layoutButtons;
        ImageView imGoiMon, imThanhToan, imAnButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenBanAn = itemView.findViewById(R.id.txtTenBanAn);
            imBanAn = itemView.findViewById(R.id.imBanAn);
            cardBanAn = itemView.findViewById(R.id.card_banan);
            layoutButtons = itemView.findViewById(R.id.layoutButtons);
            imGoiMon = itemView.findViewById(R.id.imGoiMon);
            imThanhToan = itemView.findViewById(R.id.imThanhToan);
            imAnButton = itemView.findViewById(R.id.imAnButton);
        }
    }

    // 2. Phương thức này tạo ra View mới (tương đương phần if (convertView == null))
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout_hienthibanan, parent, false);
        return new ViewHolder(view);
    }

    // 3. Phương thức này gán dữ liệu vào View (tương đương phần còn lại của getView)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BanAnDTO banAn = banAnDTOList.get(position);
        holder.txtTenBanAn.setText(banAn.getTenBan());

        // Logic hiển thị trạng thái và phân quyền (giống hệt code cũ)
        if ("true".equals(banAn.getTinhTrang())) {
            holder.cardBanAn.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorOccupied));
        } else {
            holder.cardBanAn.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorFree));
        }

        boolean isExpanded = (position == expandedPosition);
        holder.layoutButtons.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // Trong AdapterHienThiBanAn.java -> getView()

        if (maquyen == Contants.QUYEN_QUANLY) {
            // Quản lý được thấy nút Xóa và long-click tên để Sửa
            holder.imAnButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            // THAY ĐỔI Ở ĐÂY: Dùng setOnLongClickListener
            holder.txtTenBanAn.setOnLongClickListener(v -> {
                if(listener != null) {
                    listener.onSuaTenClick(holder.getAdapterPosition());
                }
                return true; // Rất quan trọng: trả về true để báo hiệu sự kiện đã được xử lý
            });

        } else {
            // Nhân viên không thấy nút Xóa và không long-click tên để Sửa được
            holder.imAnButton.setVisibility(View.GONE);
            holder.txtTenBanAn.setOnLongClickListener(null); // Vô hiệu hóa long click
        }

        // 4. Sửa lại cách xử lý click
        holder.imBanAn.setOnClickListener(v -> {
            int oldPosition = expandedPosition;
            expandedPosition = isExpanded ? -1 : holder.getAdapterPosition();

            // Thông báo cho RecyclerView cập nhật lại item cũ và mới
            // Đây là điểm mấu chốt giúp nó hoạt động mượt mà
            if(oldPosition != -1) notifyItemChanged(oldPosition);
            if(expandedPosition != -1) notifyItemChanged(expandedPosition);
        });

        // Gán sự kiện cho các nút, dùng holder.getAdapterPosition() để lấy vị trí chính xác
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

    // 5. Phương thức này trả về số lượng item
    @Override
    public int getItemCount() {
        return banAnDTOList.size();
    }
}