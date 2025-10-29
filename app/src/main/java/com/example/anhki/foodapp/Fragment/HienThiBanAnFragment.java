
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
import com.example.anhki.foodapp.DangNhapActivity;
import com.example.anhki.foodapp.ThanhToanActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiBanAn; // Adapter phiên bản RecyclerView

import com.example.anhki.foodapp.DTO.BanAnDTO;
import com.example.anhki.foodapp.R;
import com.example.anhki.foodapp.SuaBanAnActivity;
import com.example.anhki.foodapp.ThemBanAnActivity;
import com.example.anhki.foodapp.TrangChuActicity;

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
        // Có thể để trống hoặc dùng cho mục đích khác nếu cần
        Log.d(TAG, "Table item clicked at position: " + position + " - Toggling buttons.");
    }
    @Override
    public void onGoiMonClick(int position) {
        // TODO: Thêm logic xử lý khi nhấn nút Gọi món ở đây
        // Lấy thông tin bàn ăn từ vị trí được click
        BanAnDTO selectedBanAn = banAnList.get(position);
        String banAnDocId = selectedBanAn.getDocumentId();
        String tenBan = selectedBanAn.getTenBan();
        String tinhTrang = selectedBanAn.getTinhTrang();
        // --- LẤY THÔNG TIN NHÂN VIÊN TRỰC TIẾP TỪ FIREBASE AUTH ---
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra an toàn xem người dùng có còn đăng nhập không
        if (currentUser == null) {
            Toast.makeText(getContext(), "Lỗi: Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
             //TODO: Có thể điều hướng về trang đăng nhập
             Intent intent = new Intent(getActivity(), DangNhapActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
            return;
        }
        String maNhanVienUid = currentUser.getUid();
        String tenDangNhap = currentUser.getEmail(); // <-- LẤY TRỰC TIẾP EMAIL/TÊN ĐĂNG NHẬP
        // --- KẾT THÚC LẤY THÔNG TIN ---

        DocumentReference banRef = db.collection("banAn").document(banAnDocId);

        // KIỂM TRA TRẠNG THÁI BÀN
        if ("false".equals(tinhTrang)) {
            // BÀN TRỐNG -> Tạo GoiMon mới và cập nhật trạng thái bàn
            Log.d(TAG, "Bàn trống, tạo gọi món mới cho bàn: " + banAnDocId);

            // Tạo dữ liệu cho GoiMon mới
            Map<String, Object> goiMonData = new HashMap<>();
            goiMonData.put("maBanRef", banRef);
            goiMonData.put("maNhanVien", maNhanVienUid);
            goiMonData.put("tenNhanVien", tenDangNhap);
            goiMonData.put("ngayGoi", Timestamp.now());
            goiMonData.put("tinhTrang", "false"); // Hóa đơn mới, chưa thanh toán
            goiMonData.put("tongTien", 0L); // Tổng tiền ban đầu là 0

            // Sử dụng WriteBatch để đảm bảo cả hai thao tác thành công hoặc thất bại cùng lúc
            WriteBatch batch = db.batch();

            // 1. Thêm document GoiMon mới
            DocumentReference newGoiMonRef = db.collection("goiMon").document(); // Tự tạo ID
            batch.set(newGoiMonRef, goiMonData);

            // 2. Cập nhật trạng thái bàn ăn thành "true" (có khách)
            batch.update(banRef, "tinhTrang", "true");

            // Thực thi batch write
            batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Tạo gọi món và cập nhật bàn thành công. Chuyển sang thực đơn.");
                        // Sau khi tạo thành công -> Chuyển sang Fragment thực đơn
                        chuyenSangHienThiThucDon(banAnDocId, tenBan);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Lỗi khi tạo gọi món hoặc cập nhật bàn", e);
                        Toast.makeText(getContext(), "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    });

        } else {
            // BÀN ĐÃ CÓ KHÁCH -> Chuyển thẳng sang Fragment thực đơn
            Log.d(TAG, "Bàn đã có khách, chuyển sang thực đơn cho bàn: " + banAnDocId);
            chuyenSangHienThiThucDon(banAnDocId, tenBan);
        }
    }
    // Hàm trợ giúp để chuyển Fragment (tránh lặp code)
    private void chuyenSangHienThiThucDon(String banAnDocId, String tenBan) {
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
//        // Xử lý khi chọn Thanh toán từ menu (do Adapter gọi về)
//        String banAnDocId = banAnList.get(position).getDocumentId();
//        // TODO: Chuyển sang màn hình thanh toán, truyền banAnDocId
//        Toast.makeText(getActivity(), "Thanh toán (Firebase) cho bàn ID: " + banAnDocId, Toast.LENGTH_SHORT).show();
        // Lấy thông tin bàn ăn
        String banAnDocId = banAnList.get(position).getDocumentId();
        String tenBan = banAnList.get(position).getTenBan();
        DocumentReference banRef = db.collection("banAn").document(banAnDocId);

        Log.d(TAG, "Nút Thanh toán được nhấn cho bàn: " + banAnDocId);

        // BƯỚC 1: KIỂM TRA XEM CÓ HÓA ĐƠN ĐANG MỞ KHÔNG
        db.collection("goiMon")
                .whereEqualTo("maBanRef", banRef)
                .whereEqualTo("tinhTrang", "false") // Tìm hóa đơn chưa thanh toán
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // BƯỚC 2A: NẾU CÓ HÓA ĐƠN -> CHUYỂN SANG MÀN HÌNH THANH TOÁN
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot goiMonDoc = task.getResult().getDocuments().get(0);
                            String goiMonDocId = goiMonDoc.getId(); // Lấy ID hóa đơn

                            Log.d(TAG, "Tìm thấy goiMon đang mở: " + goiMonDocId + ". Chuyển sang ThanhToanActivity.");

                            Intent iThanhToan = new Intent(getContext(), ThanhToanActivity.class);
                            iThanhToan.putExtra("banAnDocId", banAnDocId); // Gửi ID bàn
                            iThanhToan.putExtra("tenBan", tenBan);        // Gửi Tên bàn
                            iThanhToan.putExtra("goiMonDocId", goiMonDocId); // Gửi ID hóa đơn
                            startActivity(iThanhToan);
                        }
                        // BƯỚC 2B: NẾU KHÔNG CÓ HÓA ĐƠN -> THÔNG BÁO
                        else {
                            Log.d(TAG, "Không tìm thấy goiMon đang mở cho bàn: " + banAnDocId);
                            Toast.makeText(getContext(), "Bàn trống hoặc đã thanh toán!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Lỗi khi kiểm tra hóa đơn
                        Log.w(TAG, "Lỗi kiểm tra hóa đơn cho bàn: " + banAnDocId, task.getException());
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra trạng thái bàn!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void xacNhanXoaBanAn(String banAnDocId, String tenBan) {
        // 1. Tạo một tham chiếu đến document bàn ăn
        DocumentReference banRef = db.collection("banAn").document(banAnDocId);

        // 2. Kiểm tra xem có hóa đơn nào đang mở ("false") liên kết với bàn này không
        db.collection("goiMon")
                .whereEqualTo("maBanRef", banRef) // Tìm theo Reference
                .whereEqualTo("tinhTrang", "false") // Trạng thái chưa thanh toán
                .limit(1) // Chỉ cần tìm 1 là đủ
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 3a. NẾU CÓ HÓA ĐƠN -> BÁO LỖI
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "Bàn đang có khách/hóa đơn, không thể xóa!", Toast.LENGTH_LONG).show();
                        }
                        // 3b. NẾU KHÔNG CÓ (BÀN TRỐNG) -> HỎI XÁC NHẬN
                        else {
                            hienThiDialogXoa(banRef, tenBan);
                        }
                    } else {
                        // Lỗi khi truy vấn
                        Log.w(TAG, "Lỗi kiểm tra hóa đơn: ", task.getException());
                        Toast.makeText(getContext(), "Lỗi kiểm tra trạng thái bàn!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void hienThiDialogXoa(DocumentReference banRef, String tenBan) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa '" + tenBan + "' không?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    // 4. Tiến hành xóa bàn
                    banRef.delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), getString(R.string.xoathanhcong), Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), getString(R.string.loi), Toast.LENGTH_SHORT).show());
                    // Listener sẽ tự động cập nhật lại danh sách
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}