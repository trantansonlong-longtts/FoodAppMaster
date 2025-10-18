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
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.anhki.foodapp.Contants;
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiDanhSachMonAn;
import com.example.anhki.foodapp.DAO.MonAnDAO;
import com.example.anhki.foodapp.DTO.MonAnDTO;
import com.example.anhki.foodapp.R;
import com.example.anhki.foodapp.SoLuongActivity;
import com.example.anhki.foodapp.ThemThucDonActivity;
import com.example.anhki.foodapp.SuaThucDonActivity; // Activity để thêm/sửa món ăn

import java.util.List;

public class HienThiDanhSachMonAnFragment extends Fragment {
    private GridView gvHienThiDSMonAn;
    private List<MonAnDTO> monAnDTOList;
    private MonAnDAO monAnDAO;
    private AdapterHienThiDanhSachMonAn adapter;
    private int maban;
    private int maloai;
    private int maquyen;

    // Launcher để nhận kết quả từ màn hình Thêm/Sửa Món ăn và SoLuong
    private final ActivityResultLauncher<Intent> monAnLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // onResume sẽ tự động cập nhật
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hienthidanhsachmonan, container, false);
        gvHienThiDSMonAn = view.findViewById(R.id.gvHienThiDanhSachMonAn);
        monAnDAO = new MonAnDAO(requireContext());

        // Lấy dữ liệu
        Bundle bundle = getArguments();
        if (bundle != null) {
            maloai = bundle.getInt("maloai", -1);
            maban = bundle.getInt("maban", 0);
        }

        // Lấy quyền
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
        maquyen = sharedPreferences.getInt("maquyen", -1);

        // PHÂN QUYỀN VÀ GÁN SỰ KIỆN
        setupGridViewListener();

        // Chỉ Quản lý mới có các chức năng quản trị
        if (maquyen == Contants.QUYEN_QUANLY && maban == 0) {
            addMenuProvider(); // Hiện nút "Thêm"
            registerForContextMenu(gvHienThiDSMonAn); // Kích hoạt "Sửa/Xóa"
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hienThiDanhSachMonAn();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (monAnDAO != null) {
            monAnDAO.close();
        }
    }

    private void hienThiDanhSachMonAn() {
        if (maloai != -1) {
            monAnDTOList = monAnDAO.LayDanhSachMonAnTheoLoai(maloai);
            adapter = new AdapterHienThiDanhSachMonAn(getContext(), R.layout.custom_layout_hienthidanhsachmonan, monAnDTOList);
            gvHienThiDSMonAn.setAdapter(adapter);
        }
    }

    private void setupGridViewListener() {
        gvHienThiDSMonAn.setOnItemClickListener((parent, view1, position, id) -> {
            MonAnDTO monAn = monAnDTOList.get(position);

            // Nếu có mã bàn, nghĩa là đang trong chế độ GỌI MÓN
            if (maban != 0) {
                Intent intent = new Intent(requireContext(), SoLuongActivity.class);
                intent.putExtra("mamon", monAn.getMaMonAn());
                intent.putExtra("maban", maban);
                monAnLauncher.launch(intent);
            }
            // Nếu không có mã bàn và là Quản lý, click có thể hiểu là Sửa (tùy chọn)
            // Nhưng để rõ ràng, ta sẽ dùng nhấn giữ (long-click) cho Sửa/Xóa
        });
    }

    private void addMenuProvider() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_them_monan, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.itThemMonAn) {
                    Intent intent = new Intent(getActivity(), ThemThucDonActivity.class);
                    intent.putExtra("maloai", maloai); // Gửi kèm mã loại để biết thêm món vào loại nào
                    monAnLauncher.launch(intent);
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
        int mamon = monAnDTOList.get(vitri).getMaMonAn();

        int id = item.getItemId();
        if (id == R.id.itSua) {
            Intent intent = new Intent(getActivity(), SuaThucDonActivity.class);
            intent.putExtra("mamon", mamon); // Gửi mã món để Activity biết là đang Sửa
            monAnLauncher.launch(intent);
            return true;
        } else if (id == R.id.itXoa) {
            if (monAnDAO.XoaMonAn(mamon)) {
                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                hienThiDanhSachMonAn();
            } else {
                Toast.makeText(getContext(), "Lỗi, xóa thất bại", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
