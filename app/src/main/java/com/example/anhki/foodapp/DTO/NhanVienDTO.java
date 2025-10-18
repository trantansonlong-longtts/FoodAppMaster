package com.example.anhki.foodapp.DTO;

public class NhanVienDTO {

    // --- Sắp xếp lại các biến cho dễ đọc ---
    // Các biến kiểu int
    private int MANV;
    private int MAQUYEN;

    // Các biến kiểu String
    private String TENDANGNHAP;
    private String MATKHAU;
    private String GIOITINH;
    private String NGAYSINH;
    private String CMND; // THAY ĐỔI QUAN TRỌNG NHẤT: Chuyển từ int sang String

    // --- Thêm Constructor để tạo đối tượng thuận tiện hơn ---

    // Constructor rỗng (mặc định)
    public NhanVienDTO() {
    }

    // Constructor đầy đủ tham số (tùy chọn, nhưng rất tiện lợi)
    public NhanVienDTO(int MANV, int MAQUYEN, String TENDANGNHAP, String MATKHAU, String GIOITINH, String NGAYSINH, String CMND) {
        this.MANV = MANV;
        this.MAQUYEN = MAQUYEN;
        this.TENDANGNHAP = TENDANGNHAP;
        this.MATKHAU = MATKHAU;
        this.GIOITINH = GIOITINH;
        this.NGAYSINH = NGAYSINH;
        this.CMND = CMND;
    }


    // --- Getters and Setters ---

    public int getMANV() {
        return MANV;
    }

    public void setMANV(int MANV) {
        this.MANV = MANV;
    }

    public int getMAQUYEN() {
        return MAQUYEN;
    }

    public void setMAQUYEN(int MAQUYEN) {
        this.MAQUYEN = MAQUYEN;
    }

    public String getTENDANGNHAP() {
        return TENDANGNHAP;
    }

    public void setTENDANGNHAP(String TENDANGNHAP) {
        this.TENDANGNHAP = TENDANGNHAP;
    }

    public String getMATKHAU() {
        return MATKHAU;
    }

    public void setMATKHAU(String MATKHAU) {
        this.MATKHAU = MATKHAU;
    }

    public String getGIOITINH() {
        return GIOITINH;
    }

    public void setGIOITINH(String GIOITINH) {
        this.GIOITINH = GIOITINH;
    }

    public String getNGAYSINH() {
        return NGAYSINH;
    }

    public void setNGAYSINH(String NGAYSINH) {
        this.NGAYSINH = NGAYSINH;
    }

    public String getCMND() {
        return CMND;
    }

    public void setCMND(String CMND) {
        this.CMND = CMND;
    }
}