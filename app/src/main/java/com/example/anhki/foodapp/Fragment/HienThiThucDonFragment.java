package com.example.anhki.foodapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.anhki.foodapp.Contants;
//import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiLoaiMonAnThucDon;
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiLoaiMonAn;
import com.example.anhki.foodapp.DAO.LoaiMonAnDAO;
import com.example.anhki.foodapp.DAO.MonAnDAO;
import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;
import com.example.anhki.foodapp.R;
import com.example.anhki.foodapp.SuaLoaiThucDonActivity;
import com.example.anhki.foodapp.ThemLoaiThucDonActivity;

import java.util.List;

public class HienThiThucDonFragment extends Fragment {
    private GridView gridView;
    private LoaiMonAnDAO loaiMonAnDAO;
    private MonAnDAO monAnDAO; // Thêm DAO để kiểm tra món ăn
    private List<LoaiMonAnDTO> loaiMonAnDTOs;
    private AdapterHienThiLoaiMonAn adapter;
    private int maban = 0;
    private int maquyen;

    // Launcher chung cho việc Thêm và Sửa để nhận kết quả
    private final ActivityResultLauncher<Intent> loaiThucDonLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // onResume() sẽ tự động tải lại dữ liệu nên không cần code ở đây
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hienthithucdon, container, false);

        if (getArguments() != null) {
            maban = getArguments().getInt("maban", 0);
        }

        gridView = view.findViewById(R.id.gvHienThiThucDon);
        loaiMonAnDAO = new LoaiMonAnDAO(requireContext());
        monAnDAO = new MonAnDAO(requireContext()); // Khởi tạo MonAnDAO

        // Lấy quyền
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
        maquyen = sharedPreferences.getInt("maquyen", -1);

        // Phân quyền cho các chức năng quản trị
        if (maquyen == Contants.QUYEN_QUANLY) {
            addMenuProvider(); // Thêm nút '+'
            registerForContextMenu(gridView); // Kích hoạt Sửa/Xóa khi nhấn giữ
        }

        // Sự kiện click để xem danh sách món ăn (dành cho mọi người)
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            LoaiMonAnDTO loai = loaiMonAnDTOs.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt("maloai", loai.getMaLoai());
            bundle.putInt("maban", maban);

            HienThiDanhSachMonAnFragment fragment = new HienThiDanhSachMonAnFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, fragment).addToBackStack(null).commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hienThiDanhSachLoaiMonAn();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loaiMonAnDAO != null) loaiMonAnDAO.close();
        if (monAnDAO != null) monAnDAO.close(); // Nhớ đóng MonAnDAO
    }

    private void hienThiDanhSachLoaiMonAn() {
        loaiMonAnDTOs = loaiMonAnDAO.LayDanhSachLoaiMonAn();
        // Adapter giờ không cần listener và maquyen nữa vì Fragment đã xử lý hết
        adapter = new AdapterHienThiLoaiMonAn(getContext(), R.layout.custom_layout_hienloaimonan, loaiMonAnDTOs);
        gridView.setAdapter(adapter);
    }

    private void addMenuProvider() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_them_loai, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.itThemLoaiThucDon) {
                    Intent intent = new Intent(getActivity(), ThemLoaiThucDonActivity.class);
                    loaiThucDonLauncher.launch(intent);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @NonNull ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.edit_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (menuInfo == null) return false;

        int vitri = menuInfo.position;
        LoaiMonAnDTO loai = loaiMonAnDTOs.get(vitri);
        int maloai = loai.getMaLoai();
        String tenloai = loai.getTenLoai();

        int id = item.getItemId();
        if (id == R.id.itSua) {
            Intent intent = new Intent(getActivity(), SuaLoaiThucDonActivity.class);
            intent.putExtra("maloai", maloai);
            intent.putExtra("tenloai", tenloai);
            loaiThucDonLauncher.launch(intent);
            return true;
        } else if (id == R.id.itXoa) {
            // KIỂM TRA AN TOÀN TRƯỚC KHI XÓA
            if (monAnDAO.KiemTraMonAnTonTaiTrongLoai(maloai)) {
                Toast.makeText(getContext(), "Không thể xóa loại đang có món ăn!", Toast.LENGTH_LONG).show();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa '" + tenloai + "' không?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            if (loaiMonAnDAO.XoaLoaiMonAn(maloai)) {
                                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                hienThiDanhSachLoaiMonAn();
                            } else {
                                Toast.makeText(getContext(), "Lỗi, xóa thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
