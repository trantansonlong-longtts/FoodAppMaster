package com.example.anhki.foodapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

// Firebase Imports
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.anhki.foodapp.Contants;
//import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiLoaiMonAnThucDon;
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiLoaiMonAn;
//import com.example.anhki.foodapp.DAO.LoaiMonAnDAO;
import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;
import com.example.anhki.foodapp.R;
import com.example.anhki.foodapp.SuaLoaiThucDonActivity;
import com.example.anhki.foodapp.ThemLoaiThucDonActivity;

import java.util.List;
import java.util.ArrayList;

public class HienThiThucDonFragment extends Fragment {
    private static final String TAG = "HienThiThucDonFrag";
    private GridView gridView;
    //private LoaiMonAnDAO loaiMonAnDAO;
    //private MonAnDAO monAnDAO; // Thêm DAO để kiểm tra món ăn
    private List<LoaiMonAnDTO> loaiMonAnList;
    private AdapterHienThiLoaiMonAn adapter;
    private int maban = 0;
    private int maquyen;

    // Firebase
    private FirebaseFirestore db;
    private ListenerRegistration loaiMonAnListener;
    // Launcher chung cho việc Thêm và Sửa để nhận kết quả
    private final ActivityResultLauncher<Intent> loaiThucDonLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // onResume() sẽ tự động tải lại dữ liệu nên không cần code ở đây
            });
    private String banAnDocId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hienthithucdon, container, false);
        if (getArguments() != null) {
            maban = getArguments().getInt("maban", 0); // Vẫn có thể giữ maban nếu cần
            banAnDocId = getArguments().getString("banAnDocId"); // Nhận ID bàn
            Log.d(TAG, "onCreateView - Nhận được banAnDocId: " + banAnDocId);
        }
        if (getArguments() != null) {
            maban = getArguments().getInt("maban", 0);
        }

        gridView = view.findViewById(R.id.gvHienThiThucDon);
        //loaiMonAnDAO = new LoaiMonAnDAO(requireContext());
        loaiMonAnList = new ArrayList<>();
        // Khởi tạo Firestore và DAO còn lại
        db = FirebaseFirestore.getInstance();
        //monAnDAO = new MonAnDAO(requireContext()); // Khởi tạo MonAnDAO

        // Lấy quyền
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
        maquyen = sharedPreferences.getInt("maquyen", -1);

        // Phân quyền cho các chức năng quản trị
        if (maquyen == Contants.QUYEN_QUANLY) {
            addMenuProvider(); // Thêm nút '+'
            registerForContextMenu(gridView); // Kích hoạt Sửa/Xóa khi nhấn giữ
        }
        // Khởi tạo Adapter rỗng ban đầu
        adapter = new AdapterHienThiLoaiMonAn(requireContext(), R.layout.custom_layout_hienloaimonan, loaiMonAnList);
        gridView.setAdapter(adapter);

        // Sự kiện click để xem danh sách món ăn (dành cho mọi người)
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            LoaiMonAnDTO loai = loaiMonAnList.get(position);
            String loaiMonAnDocId = loai.getDocumentId();

            Bundle bundle = new Bundle();
            bundle.putString("loaiMonAnDocId", loaiMonAnDocId);
            bundle.putString("banAnDocId", banAnDocId); // <-- THÊM DÒNG NÀY (dùng biến thành viên)
            // bundle.putInt("maban", maban); // Có thể bỏ dòng này nếu dùng banAnDocId

            HienThiDanhSachMonAnFragment fragment = new HienThiDanhSachMonAnFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, fragment).addToBackStack(null).commit();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listenForLoaiMonAnUpdates();
    }
    // Dừng lắng nghe trong onStop
    @Override
    public void onStop() {
        super.onStop();
        if (loaiMonAnListener != null) {
            loaiMonAnListener.remove();
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        hienThiDanhSachLoaiMonAn();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        //if (loaiMonAnDAO != null) loaiMonAnDAO.close();
//        if (monAnDAO != null) monAnDAO.close(); // Nhớ đóng MonAnDAO
//    }
    private void listenForLoaiMonAnUpdates() {
        if (loaiMonAnListener != null) {
            loaiMonAnListener.remove();
        }
        loaiMonAnListener = db.collection("loaiMonAn")
                // .orderBy("tenLoai", Query.Direction.ASCENDING) // Optional: Sắp xếp
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Lỗi lắng nghe loại món ăn:", e);
                        return;
                    }
                    if (snapshots != null) {
                        loaiMonAnList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            try {
                                // Map Firestore document sang DTO
                                LoaiMonAnDTO loai = doc.toObject(LoaiMonAnDTO.class);
                                loai.setDocumentId(doc.getId()); // Lưu lại Document ID
                                // Gán MaLoai tạm = 0 nếu cần
                                loai.setMaLoai(0);
                                loaiMonAnList.add(loai);
                            } catch(Exception convertError){
                                Log.e(TAG, "Lỗi convert loại món ăn: "+ doc.getId(), convertError);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật GridView
                        Log.d(TAG,"Danh sách loại món ăn cập nhật: " + loaiMonAnList.size() + " items");
                    }
                });
    }

//    private void hienThiDanhSachLoaiMonAn() {
//        loaiMonAnDTOs = loaiMonAnDAO.LayDanhSachLoaiMonAn();
//        // Adapter giờ không cần listener và maquyen nữa vì Fragment đã xử lý hết
//        adapter = new AdapterHienThiLoaiMonAn(getContext(), R.layout.custom_layout_hienloaimonan, loaiMonAnDTOs);
//        gridView.setAdapter(adapter);
//    }

    // --- Các phương thức Menu (Logic Thêm/Sửa/Xóa cần refactor) ---
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
                    // TODO: Refactor ThemLoaiThucDonActivity dùng Firestore
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
        LoaiMonAnDTO loai = loaiMonAnList.get(vitri);
        String loaiMonAnDocId = loai.getDocumentId();
        String tenloai = loai.getTenLoai();

        int id = item.getItemId();
        if (id == R.id.itSua) {
            Intent intent = new Intent(getActivity(), SuaLoaiThucDonActivity.class);
            intent.putExtra("loaiMonAnDocId", loaiMonAnDocId); // Truyền Document ID
            // TODO: Refactor SuaLoaiThucDonActivity dùng Firestore
            loaiThucDonLauncher.launch(intent);
            return true;
        } else if (id == R.id.itXoa) {
            // KIỂM TRA AN TOÀN TRƯỚC KHI XÓA
            xacNhanXoaLoaiMonAn(loaiMonAnDocId, tenloai); // Gọi hàm xóa Firestore
            return true;
        }
        return super.onContextItemSelected(item);
    }
    // Hàm xác nhận và xóa loại món ăn trên Firestore
    private void xacNhanXoaLoaiMonAn(String loaiMonAnDocId, String tenloai) {
        // Tạo Reference đến document loại món ăn
        DocumentReference loaiRef = db.collection("loaiMonAn").document(loaiMonAnDocId);

        // BƯỚC 1: KIỂM TRA MÓN ĂN TỒN TẠI TRÊN FIRESTORE
        db.collection("monAn")
                .whereEqualTo("maLoaiRef", loaiRef) // Tìm món ăn có maLoaiRef trỏ đến loại này
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // BƯỚC 2A: NẾU CÓ MÓN ĂN -> BÁO LỖI
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "Không thể xóa loại đang có món ăn!", Toast.LENGTH_LONG).show();
                        }
                        // BƯỚC 2B: NẾU KHÔNG CÓ MÓN ĂN -> HIỆN DIALOG XÁC NHẬN
                        else {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Xác nhận xóa")
                                    .setMessage("Bạn có chắc chắn muốn xóa '" + tenloai + "' không?")
                                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                                        // BƯỚC 3: TIẾN HÀNH XÓA LOẠI MÓN ĂN
                                        loaiRef.delete()
                                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi, xóa thất bại!", Toast.LENGTH_SHORT).show());
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                        }
                    } else {
                        Log.w(TAG, "Lỗi kiểm tra món ăn: ", task.getException());
                        Toast.makeText(getContext(), "Lỗi kiểm tra dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
