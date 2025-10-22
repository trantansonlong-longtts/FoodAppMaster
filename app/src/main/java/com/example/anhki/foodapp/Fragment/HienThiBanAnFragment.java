
package com.example.anhki.foodapp.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager; // Import GridLayoutManager
import androidx.recyclerview.widget.RecyclerView;    // Import RecyclerView

// Firebase Imports
import com.example.anhki.foodapp.Contants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiBanAn; // Adapter phiên bản RecyclerView
// Bỏ DAO không cần thiết
// import com.example.anhki.foodapp.DAO.BanAnDAO;
// import com.example.anhki.foodapp.DAO.GoiMonDAO;
import com.example.anhki.foodapp.DTO.BanAnDTO;
import com.example.anhki.foodapp.R;
import com.example.anhki.foodapp.SuaBanAnActivity;
import com.example.anhki.foodapp.ThemBanAnActivity;
import com.example.anhki.foodapp.TrangChuActicity;
// Import các Activity/Fragment khác nếu cần

import java.util.ArrayList;
import java.util.List;

// Implement interface từ Adapter phiên bản RecyclerView
public class HienThiBanAnFragment extends Fragment implements AdapterHienThiBanAn.BanAnClickListener {
    private static final String TAG = "HienThiBanAnFragment";

    private RecyclerView rvHienThiBanAn; // Đổi thành RecyclerView
    private List<BanAnDTO> banAnList;
    private int maquyen;
    private AdapterHienThiBanAn adapter; // Adapter phiên bản RecyclerView

    // Firebase
    private FirebaseFirestore db;
    private ListenerRegistration banAnListener;

    // Activity Result Launchers (giữ nguyên)
    private final ActivityResultLauncher<Intent> themBanAnLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> { });
    private final ActivityResultLauncher<Intent> suaBanAnLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> { });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hienthibanan, container, false);
        ((TrangChuActicity) requireActivity()).getSupportActionBar().setTitle(R.string.banan);

        rvHienThiBanAn = view.findViewById(R.id.gvHienBanAn); // Ánh xạ RecyclerView
        banAnList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        // goiMonDAO có thể không cần ở đây nữa nếu kiểm tra xóa dùng Firestore

        // Lấy quyền (giữ nguyên)
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
        maquyen = sharedPreferences.getInt("maquyen", -1);

        // THIẾT LẬP RECYCLERVIEW
        // Dùng GridLayoutManager để tạo giao diện lưới, ví dụ 3 cột
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rvHienThiBanAn.setLayoutManager(layoutManager);

        // Khởi tạo Adapter rỗng ban đầu, truyền maquyen và listener
        adapter = new AdapterHienThiBanAn(requireContext(), R.layout.custom_layout_hienthibanan, banAnList, maquyen, this);
        rvHienThiBanAn.setAdapter(adapter);

        // Phân quyền cho nút Thêm trên Toolbar
        if (maquyen == Contants.QUYEN_QUANLY) {
            addMenuProvider();
            // Không cần registerForContextMenu nữa, Adapter sẽ xử lý long-click
        }

        return view;
    }

    // Lắng nghe dữ liệu trong onStart
    @Override
    public void onStart() {
        super.onStart();
        listenForBanAnUpdates();
    }

    // Dừng lắng nghe trong onStop
    @Override
    public void onStop() {
        super.onStop();
        if (banAnListener != null) {
            banAnListener.remove();
        }
    }

    // Không cần đóng DAO nữa
    // @Override
    // public void onDestroyView() { ... }

    private void listenForBanAnUpdates() {
        if (banAnListener != null) {
            banAnListener.remove();
        }

        banAnListener = db.collection("banAn")
                .orderBy("tenBan", Query.Direction.ASCENDING) // Sắp xếp theo tên bàn
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Lỗi lắng nghe bàn ăn:", e);
                        return;
                    }
                    if (snapshots != null) {
                        banAnList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            try {
                                BanAnDTO banAn = doc.toObject(BanAnDTO.class); // Tự động map vào DTO nếu tên trường khớp
                                banAn.setDocumentId(doc.getId()); // Lưu Document ID
                                banAnList.add(banAn);
                            } catch (Exception conversionError){
                                Log.e(TAG, "Lỗi chuyển đổi dữ liệu bàn: " + doc.getId(), conversionError);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                        Log.d(TAG, "Danh sách bàn ăn đã cập nhật: " + banAnList.size() + " bàn");
                    }
                });
    }

    // MenuProvider cho nút Thêm (giữ nguyên)
    private void addMenuProvider() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_them, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.itThemBanAn) {
                    Intent iThemBanAn = new Intent(getActivity(), ThemBanAnActivity.class);
                    // TODO: Refactor ThemBanAnActivity dùng Firestore
                    themBanAnLauncher.launch(iThemBanAn);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    // === CÁC HÀM XỬ LÝ SỰ KIỆN TỪ ADAPTER ===

    @Override
    public void onTableClick(int position) {
        // Xử lý khi click vào bàn ăn -> Chuyển sang Fragment gọi món
        String banAnDocId = banAnList.get(position).getDocumentId();
        String tenBan = banAnList.get(position).getTenBan();

        HienThiThucDonFragment hienThiThucDonFragment = new HienThiThucDonFragment();
        Bundle bundle = new Bundle();
        bundle.putString("banAnDocId", banAnDocId);
        bundle.putString("tenBan", tenBan);
        hienThiThucDonFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, hienThiThucDonFragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onGoiMonClick(int position) {
        // TODO: Thêm logic xử lý khi nhấn nút Gọi món ở đây
        // Ví dụ: Lấy mã bàn và chuyển sang màn hình Gọi món
        String banAnDocId = banAnList.get(position).getDocumentId();
        Toast.makeText(getContext(), "Xử lý Gọi món cho bàn ID: " + banAnDocId, Toast.LENGTH_SHORT).show();

        // Hoặc bạn có thể gọi lại logic chuyển Fragment như trong onTableClick nếu cần
        // onTableClick(position);
    }

    @Override
    public void onSuaTenClick(int position) {
        // Xử lý khi chọn Sửa từ menu (do Adapter gọi về)
        String banAnDocId = banAnList.get(position).getDocumentId();
        Intent intent = new Intent(getActivity(), SuaBanAnActivity.class);
        intent.putExtra("banAnDocId", banAnDocId);
        // TODO: Refactor SuaBanAnActivity dùng Firestore
        suaBanAnLauncher.launch(intent);
    }

    @Override
    public void onXoaClick(int position) {
        // Xử lý khi chọn Xóa từ menu (do Adapter gọi về)
        String banAnDocId = banAnList.get(position).getDocumentId();
        String tenBan = banAnList.get(position).getTenBan();
        xacNhanXoaBanAn(banAnDocId, tenBan);
    }

    @Override
    public void onThanhToanClick(int position) {
        // Xử lý khi chọn Thanh toán từ menu (do Adapter gọi về)
        String banAnDocId = banAnList.get(position).getDocumentId();
        // TODO: Chuyển sang màn hình thanh toán, truyền banAnDocId
        Toast.makeText(getActivity(), "Thanh toán (Firebase) cho bàn ID: " + banAnDocId, Toast.LENGTH_SHORT).show();
    }

    // Hàm xác nhận xóa (giữ nguyên logic kiểm tra GoiMon nếu cần refactor sang Firestore)
    private void xacNhanXoaBanAn(String banAnDocId, String tenBan) {
        // BƯỚC 1: KIỂM TRA HÓA ĐƠN CHƯA THANH TOÁN TRÊN FIRESTORE
        db.collection("goiMon")
                .whereEqualTo("maBanRef", db.collection("banAn").document(banAnDocId)) // Tìm theo Reference đến bàn
                .whereEqualTo("tinhTrang", "false") // Chỉ tìm hóa đơn chưa thanh toán
                .limit(1) // Chỉ cần tìm 1 là đủ
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // BƯỚC 2A: NẾU CÓ HÓA ĐƠN -> BÁO LỖI
                        if (!task.getResult().isEmpty()) {
                            Log.w(TAG, "Không thể xóa bàn " + banAnDocId + " vì có hóa đơn chưa thanh toán.");
                            Toast.makeText(getContext(), "Bàn đang có khách/hóa đơn chưa thanh toán, không thể xóa!", Toast.LENGTH_LONG).show();
                        }
                        // BƯỚC 2B: NẾU KHÔNG CÓ HÓA ĐƠN -> HIỆN DIALOG XÁC NHẬN XÓA
                        else {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Xác nhận xóa")
                                    .setMessage("Bạn có chắc chắn muốn xóa '" + tenBan + "' không?")
                                    .setIcon(R.drawable.ic_warning) // Đảm bảo bạn có icon này
                                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                                        // BƯỚC 3: TIẾN HÀNH XÓA BÀN ĂN TRÊN FIRESTORE
                                        db.collection("banAn").document(banAnDocId)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d(TAG, "Xóa bàn Firestore thành công: " + banAnDocId);
                                                    Toast.makeText(getContext(), getString(R.string.xoathanhcong), Toast.LENGTH_SHORT).show();
                                                    // Listener sẽ tự động cập nhật GridView
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w(TAG, "Lỗi xóa bàn Firestore: " + banAnDocId, e);
                                                    Toast.makeText(getContext(), getString(R.string.loi), Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                        }
                    } else {
                        // Lỗi khi kiểm tra hóa đơn
                        Log.w(TAG, "Lỗi kiểm tra hóa đơn cho bàn: " + banAnDocId, task.getException());
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra trạng thái bàn!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Bỏ các hàm listener không cần thiết từ Adapter cũ
    // @Override public void onGoiMonClick(int position) { }
}