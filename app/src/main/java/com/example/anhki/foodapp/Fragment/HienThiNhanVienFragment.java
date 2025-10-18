package com.example.anhki.foodapp.Fragment;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.anhki.foodapp.Contants;
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiNhanVien;
import com.example.anhki.foodapp.DAO.NhanVienDAO;
import com.example.anhki.foodapp.DAO.QuyenDAO;
import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.DangKyActivity;
import com.example.anhki.foodapp.R;

import java.util.List;

public class HienThiNhanVienFragment extends Fragment {
    // ... (các biến đã có)
    private static final String PREFS_NAME = "luuquyen";
    private static final String KEY_MAQUYEN = "maquyen";

    private ListView listNhanVien;
    private NhanVienDAO nhanVienDAO;
    private QuyenDAO quyenDAO;
    private List<NhanVienDTO> nhanVienDTOList;
    private int maquyen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hienthinhanvien, container, false);
        setHasOptionsMenu(true);

        listNhanVien = view.findViewById(R.id.listNhanVien);

        FragmentActivity activity = requireActivity();
        nhanVienDAO = new NhanVienDAO(activity);
        quyenDAO = new QuyenDAO(activity);

        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        maquyen = sharedPreferences.getInt(KEY_MAQUYEN, -1);

        // CHỈ QUẢN LÝ MỚI CÓ QUYỀN SỬA/XÓA
        if (maquyen == Contants.QUYEN_QUANLY) {
            registerForContextMenu(listNhanVien);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hienThiDanhSachNhanVien();
    }

    private void hienThiDanhSachNhanVien() {
        nhanVienDTOList = nhanVienDAO.LayDanhSachNhanVien();
        AdapterHienThiNhanVien adapter = new AdapterHienThiNhanVien(getActivity(), R.layout.custom_layout_hienthinhanvien, nhanVienDTOList, quyenDAO);
        listNhanVien.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (nhanVienDAO != null) nhanVienDAO.close();
        if (quyenDAO != null) quyenDAO.close();
    }

    // --- Các phương thức menu trên Toolbar ---
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (maquyen == Contants.QUYEN_QUANLY) {
            inflater.inflate(R.menu.item_menu, menu); // Menu có nút Thêm
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itThemNhanVien) {
            Intent iDangKy = new Intent(getActivity(), DangKyActivity.class);
            startActivity(iDangKy);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // === CÁC PHƯƠNG THỨC MỚI ĐỂ SỬA/XÓA ===

    // 1. Tạo menu khi nhấn giữ
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // "Thổi phồng" file edit_context_menu.xml đã tạo ở Bước 1
        requireActivity().getMenuInflater().inflate(R.menu.edit_context_menu, menu);
    }

    // 2. Xử lý sự kiện khi chọn Sửa hoặc Xóa
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Lấy thông tin về item được nhấn giữ
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (menuInfo == null) return false;

        // Lấy vị trí và mã nhân viên tương ứng
        int vitri = menuInfo.position;
        int manhanvien = nhanVienDTOList.get(vitri).getMANV();

        int itemId = item.getItemId();
        if (itemId == R.id.itSua) {
            // Chuyển sang màn hình Đăng Ký với mã nhân viên để sửa
            Intent iDangKy = new Intent(requireActivity(), DangKyActivity.class);
            iDangKy.putExtra("manhanvien", manhanvien);
            startActivity(iDangKy);
            return true;

        } else if (itemId == R.id.itXoa) {
            // Gọi DAO để xóa
            boolean kiemtra = nhanVienDAO.XoaNhanVien(manhanvien);
            if (kiemtra) {
                Toast.makeText(getActivity(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                // Tải lại danh sách để cập nhật giao diện
                hienThiDanhSachNhanVien();
            } else {
                Toast.makeText(getActivity(), "Lỗi, xóa thất bại!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onContextItemSelected(item);
    }
}