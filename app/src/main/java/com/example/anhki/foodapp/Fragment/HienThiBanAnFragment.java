//gemini update
package com.example.anhki.foodapp.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager; // Thêm import
import androidx.recyclerview.widget.RecyclerView;    // Thêm import


import com.example.anhki.foodapp.Contants;
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiBanAn;
import com.example.anhki.foodapp.DAO.BanAnDAO;
import com.example.anhki.foodapp.DAO.GoiMonDAO;
import com.example.anhki.foodapp.DTO.BanAnDTO;
import com.example.anhki.foodapp.DTO.GoiMonDTO;
import com.example.anhki.foodapp.R;
import com.example.anhki.foodapp.SuaBanAnActivity;
import com.example.anhki.foodapp.ThanhToanActivity;
import com.example.anhki.foodapp.ThemBanAnActivity;
import com.example.anhki.foodapp.TrangChuActicity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Implement interface đã tạo trong Adapter
public class HienThiBanAnFragment extends Fragment implements AdapterHienThiBanAn.BanAnClickListener {

    //private GridView gvHienThiBanAn;
    private RecyclerView gvHienThiBanAn; // THAY ĐỔI: GridView -> RecyclerView
    private List<BanAnDTO> banAnDTOList;
    private BanAnDAO banAnDAO;
    private GoiMonDAO goiMonDAO; // Thêm DAO này để xử lý
    private int maquyen;
    private int manhanvien;
    private AdapterHienThiBanAn adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hienthibanan, container, false);
        ((TrangChuActicity) requireActivity()).getSupportActionBar().setTitle(R.string.banan);

        gvHienThiBanAn = view.findViewById(R.id.gvHienBanAn);
        // THIẾT LẬP CHO RECYCLERVIEW
        // Ở đây ta dùng GridLayoutManager để tạo giao diện lưới, ví dụ 3 cột
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        gvHienThiBanAn.setLayoutManager(layoutManager);
        banAnDAO = new BanAnDAO(requireContext());
        goiMonDAO = new GoiMonDAO(requireContext()); // Khởi tạo GoiMonDAO

        // Lấy quyền và mã nhân viên
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
        maquyen = sharedPreferences.getInt("maquyen", -1);
        manhanvien = requireActivity().getIntent().getIntExtra("manhanvien", 0); // Lấy mã nhân viên

        // 2. DÙNG MÃ QUYỀN ĐỂ BẬT/TẮT CHỨC NĂNG
        // Đoạn code này sẽ kiểm tra quyền và tự động thêm menu vào Toolbar
        if (maquyen == Contants.QUYEN_QUANLY) { // Giả sử Quản lý có mã quyền là 1

            requireActivity().addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    // HIỂN THỊ nút bấm từ file menu_them.xml của bạn
                    menuInflater.inflate(R.menu.menu_them, menu);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    // XỬ LÝ HÀNH ĐỘNG khi nhấn vào nút "Thêm bàn ăn"
                    if (menuItem.getItemId() == R.id.itThemBanAn) {
                        Intent iThemBanAn = new Intent(getActivity(), ThemBanAnActivity.class);
                        startActivity(iThemBanAn); // Mở màn hình Thêm Bàn Ăn
                        return true; // Báo hiệu đã xử lý xong
                    }
                    return false;
                }
            }, getViewLifecycleOwner());

        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        HienThiDanhSachBanAn();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (banAnDAO != null) banAnDAO.close();
        if (goiMonDAO != null) goiMonDAO.close(); // Nhớ đóng GoiMonDAO
    }

    private void HienThiDanhSachBanAn() {
        banAnDTOList = banAnDAO.LayTatCaBanAn();
        adapter = new AdapterHienThiBanAn(getContext(), R.layout.custom_layout_hienthibanan, banAnDTOList, maquyen, this);
        gvHienThiBanAn.setAdapter(adapter);
    }

    // === CÁC HÀM XỬ LÝ LOGIC ĐƯỢC GỌI TỪ ADAPTER ===

    @Override
    public void onTableClick(int position) {

    }

    @Override
    public void onGoiMonClick(int position) {
        int maban = banAnDTOList.get(position).getMaBan();
        String tinhtrang = banAnDTOList.get(position).getTinhTrang();

        // Nếu bàn trống, tạo một GoiMon mới và cập nhật trạng thái bàn
        if ("false".equals(tinhtrang)) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
            String ngaygoi = dateFormat.format(calendar.getTime());

            GoiMonDTO goiMonDTO = new GoiMonDTO();
            goiMonDTO.setMaBan(maban);
            goiMonDTO.setMaNhanVien(manhanvien);
            goiMonDTO.setNgayGoi(ngaygoi);
            goiMonDTO.setTinhTrang("false");

            goiMonDAO.ThemGoiMon(goiMonDTO);
            banAnDAO.CapNhatTinhTrangBan(maban, "true");
        }

        // Chuyển sang Fragment thực đơn
        FragmentTransaction tranThucDonTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        HienThiThucDonFragment hienThiThucDonFragment = new HienThiThucDonFragment();

        Bundle bDuLieuThucDon = new Bundle();
        bDuLieuThucDon.putInt("maban", maban);
        hienThiThucDonFragment.setArguments(bDuLieuThucDon);

        tranThucDonTransaction.replace(R.id.content, hienThiThucDonFragment).addToBackStack("hienthibanan");
        tranThucDonTransaction.commit();
    }

    @Override
    public void onThanhToanClick(int position) {
        int maban = banAnDTOList.get(position).getMaBan();
        int magoimon = (int) goiMonDAO.LayMaGoiMonTheoMaBan(maban, "false");

        if (magoimon != 0) {
            Intent iThanhToan = new Intent(getContext(), ThanhToanActivity.class);
            iThanhToan.putExtra("maban", maban);
            iThanhToan.putExtra("magoimon", magoimon);
            startActivity(iThanhToan);
        } else {
            Toast.makeText(getContext(), "Bàn trống, không có gì để thanh toán", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuaTenClick(int position) {
        int maban = banAnDTOList.get(position).getMaBan();
        Intent intent = new Intent(getActivity(), SuaBanAnActivity.class);
        intent.putExtra("maban", maban);
        startActivity(intent);
    }


    @Override
    public void onXoaClick(int position) {
        // Lấy thông tin bàn ăn
        int maban = banAnDTOList.get(position).getMaBan();
        String tenban = banAnDTOList.get(position).getTenBan();

        // BƯỚC 1: KIỂM TRA TRẠNG THÁI BÀN NGAY LẬP TỨC
        int magoimon = (int) goiMonDAO.LayMaGoiMonTheoMaBan(maban, "false");

        // BƯỚC 2A: NẾU BÀN CÓ KHÁCH -> BÁO LỖI VÀ DỪNG LẠI
        if (magoimon != 0) {
            Toast.makeText(getContext(), "Bàn đang có khách và chưa thanh toán, không thể xóa!", Toast.LENGTH_LONG).show();
        }
        // BƯỚC 2B: NẾU BÀN TRỐNG -> HIỂN THỊ HỘP THOẠI XÁC NHẬN
        else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa bàn ăn '" + tenban + "' không?")
                    //.setIcon(R.drawable.ic_warning)
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        // Người dùng đã xác nhận, tiến hành xóa
                        if (banAnDAO.XoaBanAn(maban)) {
                            Toast.makeText(getContext(), getString(R.string.xoathanhcong), Toast.LENGTH_SHORT).show();
                            HienThiDanhSachBanAn(); // Tải lại danh sách
                        } else {
                            Toast.makeText(getContext(), getString(R.string.loi), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }
}