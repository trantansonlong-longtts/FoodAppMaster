package com.example.anhki.foodapp.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anhki.foodapp.CustomAdapter.AdapterBaoCaoDoanhThu;
import com.example.anhki.foodapp.DTO.BaoCaoDTO;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Firebase Imports
import com.google.firebase.Timestamp; // Import Timestamp
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.anhki.foodapp.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BaoCaoDoanhThuFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BaoCaoDoanhThuFrag";
    private RecyclerView rvDanhSachHoaDon;
    private List<BaoCaoDTO> hoaDonList;
    private AdapterBaoCaoDoanhThu adapterBaoCao;
    // Views
    private Button btnChonNgayBatDau, btnChonNgayKetThuc;
    private Button btnHomNay, btnTuanNay, btnThangNay;
    private Button btnXemBaoCao;
    private TextView txtHienThiTongDoanhThu;

    // Firebase
    private FirebaseFirestore db;

    // Biến lưu trữ ngày đã chọn
    private Calendar ngayBatDau;
    private Calendar ngayKetThuc;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_baocao_doanhthu, container, false);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ Views
        btnChonNgayBatDau = view.findViewById(R.id.btnChonNgayBatDau);
        btnChonNgayKetThuc = view.findViewById(R.id.btnChonNgayKetThuc);
        btnHomNay = view.findViewById(R.id.btnHomNay);
        btnTuanNay = view.findViewById(R.id.btnTuanNay);
        btnThangNay = view.findViewById(R.id.btnThangNay);
        btnXemBaoCao = view.findViewById(R.id.btnXemBaoCao);
        txtHienThiTongDoanhThu = view.findViewById(R.id.txtHienThiTongDoanhThu);
        rvDanhSachHoaDon = view.findViewById(R.id.rvDanhSachHoaDon);

        //khởi tạo Recycle View
        hoaDonList = new ArrayList<>();
        adapterBaoCao = new AdapterBaoCaoDoanhThu(getContext(), hoaDonList);
        rvDanhSachHoaDon.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDanhSachHoaDon.setAdapter(adapterBaoCao);


        // Khởi tạo ngày mặc định (ví dụ: hôm nay)
        ngayBatDau = Calendar.getInstance();
        ngayKetThuc = Calendar.getInstance();
        updateDateButtonText();

        // Gán sự kiện click
        btnChonNgayBatDau.setOnClickListener(this);
        btnChonNgayKetThuc.setOnClickListener(this);
        btnHomNay.setOnClickListener(this);
        btnTuanNay.setOnClickListener(this);
        btnThangNay.setOnClickListener(this);
        btnXemBaoCao.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnChonNgayBatDau) {
            showDatePickerDialog(true); // true = chọn ngày bắt đầu
        } else if (id == R.id.btnChonNgayKetThuc) {
            showDatePickerDialog(false); // false = chọn ngày kết thúc
        } else if (id == R.id.btnHomNay) {
            setDatesToToday();
        } else if (id == R.id.btnTuanNay) {
            setDatesToThisWeek();
        } else if (id == R.id.btnThangNay) {
            setDatesToThisMonth();
        } else if (id == R.id.btnXemBaoCao) {
            tinhToanVaHienThiDoanhThu();
        }
    }

    // Hiển thị DatePickerDialog
    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendarToSet = isStartDate ? ngayBatDau : ngayKetThuc;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    if (isStartDate) {
                        ngayBatDau = selectedDate;
                        // Đảm bảo ngày bắt đầu không sau ngày kết thúc
                        if (ngayBatDau.after(ngayKetThuc)) {
                            ngayKetThuc = (Calendar) ngayBatDau.clone();
                        }
                    } else {
                        ngayKetThuc = selectedDate;
                        // Đảm bảo ngày kết thúc không trước ngày bắt đầu
                        if (ngayKetThuc.before(ngayBatDau)) {
                            ngayBatDau = (Calendar) ngayKetThuc.clone();
                        }
                    }
                    updateDateButtonText();
                },
                calendarToSet.get(Calendar.YEAR),
                calendarToSet.get(Calendar.MONTH),
                calendarToSet.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Cập nhật text trên Button ngày
    private void updateDateButtonText() {
        btnChonNgayBatDau.setText(dateFormat.format(ngayBatDau.getTime()));
        btnChonNgayKetThuc.setText(dateFormat.format(ngayKetThuc.getTime()));
    }

    // --- Các hàm chọn nhanh ---
    private void setDatesToToday() {
        ngayBatDau = Calendar.getInstance();
        ngayKetThuc = Calendar.getInstance();
        updateDateButtonText();
    }

    private void setDatesToThisWeek() {
        ngayBatDau = Calendar.getInstance();
        ngayBatDau.set(Calendar.DAY_OF_WEEK, ngayBatDau.getFirstDayOfWeek()); // Về đầu tuần
        ngayKetThuc = (Calendar) ngayBatDau.clone();
        ngayKetThuc.add(Calendar.DAY_OF_WEEK, 6); // Đến cuối tuần
        updateDateButtonText();
    }

    private void setDatesToThisMonth() {
        ngayBatDau = Calendar.getInstance();
        ngayBatDau.set(Calendar.DAY_OF_MONTH, 1); // Về đầu tháng
        ngayKetThuc = Calendar.getInstance();
        ngayKetThuc.set(Calendar.DAY_OF_MONTH, ngayKetThuc.getActualMaximum(Calendar.DAY_OF_MONTH)); // Đến cuối tháng
        updateDateButtonText();
    }

    // --- Hàm chính: Truy vấn và Tính toán ---
    private void tinhToanVaHienThiDoanhThu() {
        // Đặt giờ phút giây cho khoảng thời gian truy vấn chính xác
        Calendar startCal = (Calendar) ngayBatDau.clone();
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        Timestamp startTimestamp = new Timestamp(startCal.getTime());

        Calendar endCal = (Calendar) ngayKetThuc.clone();
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        Timestamp endTimestamp = new Timestamp(endCal.getTime());

        Log.d(TAG, "Truy vấn doanh thu từ: " + startTimestamp.toDate() + " đến: " + endTimestamp.toDate());

        // Hiện loading (nếu có)
        txtHienThiTongDoanhThu.setText("Đang tính toán...");
        //xóa ds cũ
        hoaDonList.clear();
        adapterBaoCao.notifyDataSetChanged();
        // Truy vấn dữ liệu
        db.collection("goiMon")
                .whereEqualTo("tinhTrang", "true") // Chỉ lấy hóa đơn đã thanh toán
                .whereGreaterThanOrEqualTo("gioThanhToan", startTimestamp) // Lọc theo ngày bắt đầu
                .whereLessThanOrEqualTo("gioThanhToan", endTimestamp)      // Lọc theo ngày kết thúc
                .orderBy("gioThanhToan", Query.Direction.ASCENDING) //sắp xếp hóa đơn mới
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long tongDoanhThu = 0;
                        if (task.getResult() == null || task.getResult().isEmpty()) {
                            txtHienThiTongDoanhThu.setText("0 ₫");
                            Toast.makeText(getContext(),"Không tìm thấy hóa đơn nào.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Duyệt qua từng hóa đơn để tính tổng
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 1. Tính tổng doanh thu
                            Long tongTienHoaDon = document.getLong("tongTien");
                            if (tongTienHoaDon != null) {
                                tongDoanhThu += tongTienHoaDon;
                            }
                            // 2. Lấy thông tin cho danh sách
                            BaoCaoDTO baoCaoItem = new BaoCaoDTO();
                            baoCaoItem.setTongTien(tongTienHoaDon != null ? tongTienHoaDon : 0);

                            // 2. ĐỊNH DẠNG NGÀY VÀ GIỜ
                            Timestamp thanhToanTimestamp = document.getTimestamp("gioThanhToan");
                            if (thanhToanTimestamp != null) {
                                baoCaoItem.setNgayGoi(dateFormat.format(thanhToanTimestamp.toDate()));
                                baoCaoItem.setGioThanhToan(timeFormat.format(thanhToanTimestamp.toDate()));
                            } else {
                                baoCaoItem.setNgayGoi("Không rõ");
                                baoCaoItem.setGioThanhToan("N/A");
                            }
                            // 3. Lấy tên bàn (Truy vấn lồng)
                            DocumentReference banRef = document.getDocumentReference("maBanRef");
                            if (banRef != null) {
                                banRef.get().addOnSuccessListener(banDoc -> {
                                    if (banDoc.exists()) {
                                        baoCaoItem.setTenBan(banDoc.getString("tenBan"));
                                    } else {
                                        baoCaoItem.setTenBan("Bàn đã xóa");
                                    }
                                    // Cập nhật lại item khi có tên bàn
                                    adapterBaoCao.notifyDataSetChanged();
                                });
                            } else {
                                baoCaoItem.setTenBan("Không rõ");
                            }
                            // LẤY TÊN NHÂN VIÊN TỪ HÓA ĐƠN
                            baoCaoItem.setTenNhanVien(document.getString("tenNhanVien"));

                            hoaDonList.add(baoCaoItem);
                        }

                        // Hiển thị tổng doanh thu
                        txtHienThiTongDoanhThu.setText(formatVND(tongDoanhThu));
                        // Cập nhật Adapter (hiển thị ngay, tên bàn sẽ cập nhật sau)
                        adapterBaoCao.notifyDataSetChanged();

                    } else {
                        // Xử lý lỗi truy vấn
                        Log.w(TAG, "Lỗi truy vấn doanh thu: ", task.getException());
                        // LỖI INDEX?
                        if (task.getException() != null && task.getException().getMessage().contains("FAILED_PRECONDITION")) {
                            Toast.makeText(getContext(), "Cần tạo Index trên Firestore, vui lòng kiểm tra Logcat!", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Lỗi Index Firestore, truy cập link sau để tạo:", task.getException());
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu báo cáo", Toast.LENGTH_SHORT).show();
                        }
                        txtHienThiTongDoanhThu.setText("Lỗi");
                    }
                });
    }

    // Hàm định dạng tiền tệ VND (giống như trong ThanhToanActivity)
    private String formatVND(long amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        return currencyFormatter.format(amount);
    }

}