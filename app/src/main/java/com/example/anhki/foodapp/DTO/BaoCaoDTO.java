package com.example.anhki.foodapp.DTO;

public class BaoCaoDTO {
    private String tenBan;
    private String ngayGoi; // Sẽ lưu dạng String đã định dạng
    private String gioThanhToan;
    private String tenNhanVien;
    private long tongTien;

    // Constructor rỗng
    public BaoCaoDTO() {}

    // Getters and Setters
    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }

    public String getTenNhanVien() { return tenNhanVien;}
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien;    }
    public String getNgayGoi() { return ngayGoi; }
    public void setNgayGoi(String ngayGoi) { this.ngayGoi = ngayGoi; }
    public String getGioThanhToan() { return gioThanhToan; }
    public void setGioThanhToan(String gioThanhToan) { this.gioThanhToan = gioThanhToan; }
    public long getTongTien() { return tongTien; }
    public void setTongTien(long tongTien) { this.tongTien = tongTien; }
}