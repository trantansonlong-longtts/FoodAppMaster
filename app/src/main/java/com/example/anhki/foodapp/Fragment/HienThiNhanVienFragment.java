package com.example.anhki.foodapp.Fragment;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

// Firebase Imports
import com.example.anhki.foodapp.SuaNhanVienActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.anhki.foodapp.Contants;
import com.example.anhki.foodapp.CustomAdapter.AdapterHienThiNhanVien;
import com.example.anhki.foodapp.DTO.NhanVienDTO;
import com.example.anhki.foodapp.DangKyActivity;
import com.example.anhki.foodapp.R;

import java.util.ArrayList;
import java.util.List;

public class HienThiNhanVienFragment extends Fragment {
    private static final String TAG = "HienThiNhanVienFrag";
    private static final String PREFS_NAME = "luuquyen";
    private static final String KEY_MAQUYEN = "maquyen";

    private ListView listNhanVien;
    private AdapterHienThiNhanVien adapter;
    private List<NhanVienDTO> nhanVienList;
    private int maquyen;
    // Firebase
    private FirebaseFirestore db;
    private ListenerRegistration nhanVienListener; // Biến để quản lý listener

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hienthinhanvien, container, false);
        setHasOptionsMenu(true); // Vẫn dùng cách này nếu chưa đổi hết sang MenuProvider

        listNhanVien = view.findViewById(R.id.listNhanVien);
        nhanVienList = new ArrayList<>(); // Khởi tạo danh sách rỗng

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy quyền từ SharedPreferences (giữ nguyên)
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        maquyen = sharedPreferences.getInt(KEY_MAQUYEN, -1);

        // Khởi tạo Adapter rỗng ban đầu
        adapter = new AdapterHienThiNhanVien(requireContext(), R.layout.custom_layout_hienthinhanvien, nhanVienList, db); // Truyền db vào adapter
        listNhanVien.setAdapter(adapter);

        // Phân quyền UI (giữ nguyên)
        if (maquyen == Contants.QUYEN_QUANLY) {
            registerForContextMenu(listNhanVien);
            // Có thể thêm MenuProvider ở đây nếu muốn
        }

        return view;
    }
    // Lắng nghe dữ liệu trong onStart()
    @Override
    public void onStart() {
        super.onStart();
        listenForNhanVienUpdates();
    }
    // Dừng lắng nghe trong onStop()
    @Override
    public void onStop() {
        super.onStop();
        if (nhanVienListener != null) {
            nhanVienListener.remove(); // Hủy đăng ký listener để tránh rò rỉ bộ nhớ
        }
    }
    private void listenForNhanVienUpdates() {
        if (nhanVienListener != null) {
            nhanVienListener.remove(); // Hủy listener cũ nếu có
        }

        nhanVienListener = db.collection("nhanVien")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Lỗi lắng nghe nhân viên:", e);
                        Toast.makeText(getContext(), "Lỗi tải danh sách nhân viên", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        nhanVienList.clear(); // Xóa danh sách cũ
                        for (QueryDocumentSnapshot doc : snapshots) {
                            try {
                                // Chuyển đổi Document Firestore thành NhanVienDTO
                                NhanVienDTO nhanVien = new NhanVienDTO();
                                nhanVien.setUID(doc.getId()); // Lưu UID làm mã nhân viên tạm thời
                                nhanVien.setTENDANGNHAP(doc.getString("tenDangNhap"));
                                nhanVien.setCMND(doc.getString("cmnd"));
                                nhanVien.setNGAYSINH(doc.getString("ngaySinh"));
                                nhanVien.setGIOITINH(doc.getString("gioiTinh"));
                                Long maQuyenLong = doc.getLong("maQuyen"); // Firestore lưu số là Long
                                nhanVien.setMAQUYEN(maQuyenLong != null ? maQuyenLong.intValue() : 0); // Chuyển về int

                                nhanVienList.add(nhanVien);
                            } catch (Exception conversionError) {
                                Log.e(TAG, "Lỗi chuyển đổi dữ liệu nhân viên: " + doc.getId(), conversionError);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật lại ListView
                        Log.d(TAG, "Danh sách nhân viên đã được cập nhật: " + nhanVienList.size() + " items");
                    } else {
                        Log.d(TAG, "Snapshot nhân viên là null");
                    }
                });
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
//            Intent iDangKy = new Intent(getActivity(), DangKyActivity.class);
//            startActivity(iDangKy);
            // TODO: Mở DangKyActivity (phiên bản Firebase)
            Toast.makeText(getContext(), "Chức năng Thêm NV (Firebase)", Toast.LENGTH_SHORT).show();
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
        //có thể thêm item reset mật khẩu tại đây
    }

    // 2. Xử lý sự kiện khi chọn Sửa hoặc Xóa
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Lấy thông tin về item được nhấn giữ
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (menuInfo == null) return false;

        // Lấy vị trí và mã nhân viên tương ứng
        int vitri = menuInfo.position;
        // Lấy UID từ DTO (đã lưu ở listenForNhanVienUpdates)
        String uid = nhanVienList.get(vitri).getUID();
        String tenDN = nhanVienList.get(vitri).getTENDANGNHAP(); // Lấy tên để hiển thị

        //
        int id = item.getItemId();
        if (id == R.id.itSua) {
            // TODO: Mở Activity Sửa NV (Firebase), truyền UID và dữ liệu hiện tại
            Toast.makeText(getContext(), "Chức năng Sửa NV (Firebase): " + uid, Toast.LENGTH_SHORT).show();
            Intent iSua = new Intent(requireActivity(), SuaNhanVienActivity.class);
            iSua.putExtra("uid", uid);
            startActivity(iSua);
            return true;
        } else if (id == R.id.itXoa) {
            xacNhanXoaNhanVien(uid, tenDN); // Gọi hàm xóa Firebase
            return true;
        }
        // TODO: Thêm case cho Đặt lại mật khẩu nếu có

        return super.onContextItemSelected(item);
    }
    // Hàm xác nhận và xóa nhân viên trên Firestore
    private void xacNhanXoaNhanVien(String uid, String tenDN) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Xóa nhân viên '" + tenDN + "'? (Chỉ xóa thông tin, không xóa tài khoản đăng nhập)")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    db.collection("nhanVien").document(uid)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Xóa nhân viên Firestore thành công: " + uid);
                                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                // Listener sẽ tự động cập nhật danh sách
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Lỗi xóa nhân viên Firestore: " + uid, e);
                                Toast.makeText(getContext(), "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
