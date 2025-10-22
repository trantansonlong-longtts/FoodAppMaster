//package com.example.anhki.foodapp.Fragment;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.ContextMenu;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.view.MenuProvider;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Lifecycle;
//
//import com.example.anhki.foodapp.Contants;
//import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiDanhSachMonAn;
//import com.example.anhki.foodapp.DAO.MonAnDAO;
//import com.example.anhki.foodapp.DTO.MonAnDTO;
//import com.example.anhki.foodapp.R;
//import com.example.anhki.foodapp.SoLuongActivity;
//import com.example.anhki.foodapp.ThemThucDonActivity;
//import com.example.anhki.foodapp.SuaThucDonActivity; // Activity để thêm/sửa món ăn
//
//import java.util.List;
//
//public class HienThiDanhSachMonAnFragment extends Fragment {
//    private GridView gvHienThiDSMonAn;
//    private List<MonAnDTO> monAnDTOList;
//    private MonAnDAO monAnDAO;
//    private AdapterHienThiDanhSachMonAn adapter;
//    private int maban;
//    private int maloai;
//    private int maquyen;
//    private String loaiMonAnDocId;
//
//    // Launcher để nhận kết quả từ màn hình Thêm/Sửa Món ăn và SoLuong
//    private final ActivityResultLauncher<Intent> monAnLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                // onResume sẽ tự động cập nhật
//            });
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.layout_hienthidanhsachmonan, container, false);
//        gvHienThiDSMonAn = view.findViewById(R.id.gvHienThiDanhSachMonAn);
//        monAnDAO = new MonAnDAO(requireContext());
//
//        // Lấy dữ liệu
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            loaiMonAnDocId = bundle.getString("loaiMonAnDocId");
//            maloai = bundle.getInt("maloai", -1);
//            maban = bundle.getInt("maban", 0);
//        }
//
//        // Lấy quyền
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
//        maquyen = sharedPreferences.getInt("maquyen", -1);
//
//        // PHÂN QUYỀN VÀ GÁN SỰ KIỆN
//        setupGridViewListener();
//
//        // Chỉ Quản lý mới có các chức năng quản trị
//        if (maquyen == Contants.QUYEN_QUANLY && maban == 0) {
//            addMenuProvider(); // Hiện nút "Thêm"
//            registerForContextMenu(gvHienThiDSMonAn); // Kích hoạt "Sửa/Xóa"
//        }
//
//        return view;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        hienThiDanhSachMonAn();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (monAnDAO != null) {
//            monAnDAO.close();
//        }
//    }
//
//    private void hienThiDanhSachMonAn() {
//        if (maloai != -1) {
//            monAnDTOList = monAnDAO.LayDanhSachMonAnTheoLoai(maloai);
//            adapter = new AdapterHienThiDanhSachMonAn(getContext(), R.layout.custom_layout_hienthidanhsachmonan, monAnDTOList);
//            gvHienThiDSMonAn.setAdapter(adapter);
//        }
//    }
//
//    private void setupGridViewListener() {
//        gvHienThiDSMonAn.setOnItemClickListener((parent, view1, position, id) -> {
//            MonAnDTO monAn = monAnDTOList.get(position);
//
//            // Nếu có mã bàn, nghĩa là đang trong chế độ GỌI MÓN
//            if (maban != 0) {
//                Intent intent = new Intent(requireContext(), SoLuongActivity.class);
//                intent.putExtra("mamon", monAn.getMaMonAn());
//                intent.putExtra("maban", maban);
//                monAnLauncher.launch(intent);
//            }
//            // Nếu không có mã bàn và là Quản lý, click có thể hiểu là Sửa (tùy chọn)
//            // Nhưng để rõ ràng, ta sẽ dùng nhấn giữ (long-click) cho Sửa/Xóa
//        });
//    }
//
//    private void addMenuProvider() {
//        requireActivity().addMenuProvider(new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                menuInflater.inflate(R.menu.menu_them_monan, menu);
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                if (menuItem.getItemId() == R.id.itThemMonAn) {
//                    Intent intent = new Intent(getActivity(), ThemThucDonActivity.class);
//
//                    // SỬA LẠI DÒNG NÀY: Gửi đúng key và đúng giá trị (String ID)
//                    intent.putExtra("loaiMonAnDocId", loaiMonAnDocId);
//
//                    monAnLauncher.launch(intent);
//                    return true;
//                }
//                return false;
//            }
//        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
//    }
//
//    @Override
//    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @NonNull ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        requireActivity().getMenuInflater().inflate(R.menu.edit_context_menu, menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        if (menuInfo == null) return false;
//
//        int vitri = menuInfo.position;
//        int mamon = monAnDTOList.get(vitri).getMaMonAn();
//
//        int id = item.getItemId();
//        if (id == R.id.itSua) {
//            Intent intent = new Intent(getActivity(), SuaThucDonActivity.class);
//            intent.putExtra("mamon", mamon); // Gửi mã món để Activity biết là đang Sửa
//            monAnLauncher.launch(intent);
//            return true;
//        } else if (id == R.id.itXoa) {
//            if (monAnDAO.XoaMonAn(mamon)) {
//                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
//                hienThiDanhSachMonAn();
//            } else {
//                Toast.makeText(getContext(), "Lỗi, xóa thất bại", Toast.LENGTH_SHORT).show();
//            }
//            return true;
//        }
//        return super.onContextItemSelected(item);
//    }
//}
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
import com.example.anhki.foodapp.SoLuongActivity;
import com.example.anhki.foodapp.SuaThucDonActivity;
import com.google.firebase.Timestamp; // Import Timestamp
import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue; // Import FieldValue
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch; // Import WriteBatch

import com.example.anhki.foodapp.Contants;
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiDanhSachMonAn;
import com.example.anhki.foodapp.DTO.MonAnDTO;
import com.example.anhki.foodapp.R;
// Bỏ SoLuongActivity nếu không dùng nữa
// import com.example.anhki.foodapp.SoLuongActivity;
import com.example.anhki.foodapp.ThemThucDonActivity;

import java.util.ArrayList;
import java.util.Date; // Import Date
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HienThiDanhSachMonAnFragment extends Fragment {
    private static final String TAG = "HienThiDSMonAnFrag";

    private GridView gvHienThiDSMonAn; // Hoặc RecyclerView
    private List<MonAnDTO> monAnDTOList;
    private AdapterHienThiDanhSachMonAn adapter;
    private String banAnDocId; // Document ID của bàn ăn
    private String loaiMonAnDocId;
    private int maquyen;
    private String maNhanVienUid; // UID của nhân viên đang đăng nhập

    // Firebase
    private FirebaseFirestore db;
    private ListenerRegistration monAnListener;

    // Launcher (giữ nguyên)
    private final ActivityResultLauncher<Intent> monAnLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> { /* onResume sẽ cập nhật */ });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hienthidanhsachmonan, container, false);
        gvHienThiDSMonAn = view.findViewById(R.id.gvHienThiDanhSachMonAn);
        monAnDTOList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu
        Bundle bundle = getArguments();
        if (bundle != null) {
            loaiMonAnDocId = bundle.getString("loaiMonAnDocId");
            banAnDocId = bundle.getString("banAnDocId"); // Nhận ID bàn ăn
        } else {
            Log.e(TAG, "Lỗi: Không nhận được loại món ăn hoặc ID bàn ăn!");
            requireActivity().getSupportFragmentManager().popBackStack();
            return view;
        }

        // Lấy quyền và UID nhân viên
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
        maquyen = sharedPreferences.getInt("maquyen", -1);
        // Lấy UID từ Intent hoặc SharedPreferences (tùy cách bạn lưu lúc đăng nhập)
        maNhanVienUid = requireActivity().getIntent().getStringExtra("uid"); // Hoặc lấy từ SharedPreferences
        if (maNhanVienUid == null || maNhanVienUid.isEmpty()){
            // Lấy từ Firebase Auth nếu đang đăng nhập
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                maNhanVienUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            } else {
                Log.e(TAG, "Lỗi: Không lấy được UID nhân viên!");
                // Nên quay lại màn hình đăng nhập hoặc xử lý lỗi
            }
        }


        // Khởi tạo Adapter
        adapter = new AdapterHienThiDanhSachMonAn(requireContext(), R.layout.custom_layout_hienthidanhsachmonan, monAnDTOList);
        gvHienThiDSMonAn.setAdapter(adapter);

        setupGridViewListener(); // Gán sự kiện click gọi món

        // Phân quyền Quản lý (giữ nguyên)
        if (maquyen == Contants.QUYEN_QUANLY && banAnDocId == null) { // Chỉ quản lý khi xem thực đơn (ko có ID bàn)
            addMenuProvider();
            registerForContextMenu(gvHienThiDSMonAn);
        }

        return view;
    }

    // --- Vòng đời và Lắng nghe dữ liệu (giữ nguyên) ---
    @Override
    public void onStart() {
        super.onStart();
        listenForMonAnUpdates();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (monAnListener != null) monAnListener.remove();
    }
    private void listenForMonAnUpdates() {
        // ... (code lắng nghe món ăn giữ nguyên)
        if (loaiMonAnDocId == null || loaiMonAnDocId.isEmpty()) return;
        if (monAnListener != null) monAnListener.remove();
        DocumentReference loaiRef = db.collection("loaiMonAn").document(loaiMonAnDocId);
        monAnListener = db.collection("monAn")
                .whereEqualTo("maLoaiRef", loaiRef)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) { Log.w(TAG, "Lỗi lắng nghe món ăn:", e); return; }
                    if (snapshots != null) {
                        monAnDTOList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            try {
                                MonAnDTO monAn = doc.toObject(MonAnDTO.class);
                                monAn.setDocumentId(doc.getId());
                                monAnDTOList.add(monAn);
                            } catch (Exception conversionError) { Log.e(TAG, "Lỗi convert món ăn: " + doc.getId(), conversionError); }
                        }
                        adapter.notifyDataSetChanged();
                    } else { monAnDTOList.clear(); adapter.notifyDataSetChanged(); }
                });
    }

    // --- Xử lý sự kiện Gọi Món ---
    private void setupGridViewListener() {
        gvHienThiDSMonAn.setOnItemClickListener((parent, view1, position, id) -> {
            MonAnDTO selectedMonAn = monAnDTOList.get(position);
            String monAnDocId = selectedMonAn.getDocumentId();

            // Chỉ xử lý khi đang ở chế độ gọi món (maban != null)
            if (banAnDocId != null && !banAnDocId.isEmpty()) {
                Intent intent = new Intent(requireContext(), SoLuongActivity.class);
                intent.putExtra("monAnDocId", monAnDocId);
                intent.putExtra("banAnDocId", banAnDocId); // Truyền ID bàn dạng String
                intent.putExtra("maNhanVienUid", maNhanVienUid); // Truyền UID nhân viên

                // Dùng monAnLauncher nếu SoLuongActivity trả về kết quả
                monAnLauncher.launch(intent);
                // Hoặc startActivity(intent); nếu không cần kết quả
            }
            // Không làm gì nếu ở chế độ quản lý
        });
    }

    // --- Logic Gọi Món Chính ---
    // --- Các phương thức Menu ---
    // MenuProvider cho nút Thêm (giữ nguyên)
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
                    intent.putExtra("loaiMonAnDocId", loaiMonAnDocId); // Gửi kèm ID loại hiện tại
                    monAnLauncher.launch(intent);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    // Context Menu cho Sửa/Xóa (giữ nguyên)
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
        String monAnDocId = monAnDTOList.get(vitri).getDocumentId(); // Lấy Document ID món ăn
        String tenMonAn = monAnDTOList.get(vitri).getTenMonAn(); // Lấy tên món

        int id = item.getItemId();
        if (id == R.id.itSua) {
            Intent intent = new Intent(getActivity(), SuaThucDonActivity.class); // Mở Activity Sửa món
            intent.putExtra("mamon", monAnDocId); // Truyền Document ID
            monAnLauncher.launch(intent);
            return true;
        } else if (id == R.id.itXoa) {
            xacNhanXoaMonAn(monAnDocId, tenMonAn); // Gọi hàm xóa Firestore
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // Hàm xác nhận và xóa món ăn trên Firestore
    private void xacNhanXoaMonAn(String monAnDocId, String tenMonAn) {
        // TODO: (Nâng cao) Kiểm tra xem món ăn này có trong hóa đơn nào chưa thanh toán không
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa '" + tenMonAn + "' không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    db.collection("monAn").document(monAnDocId)
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi, xóa thất bại", Toast.LENGTH_SHORT).show());
                    // Listener sẽ tự cập nhật danh sách
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}