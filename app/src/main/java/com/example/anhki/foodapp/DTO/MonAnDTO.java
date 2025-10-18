package com.example.anhki.foodapp.DTO;

public class MonAnDTO {
    private int MaMonAn, MaLoai;
    private String TenMonAn;
    private int GiaTien;
    // Trong MonAnDTO.java
    private byte[] HinhAnh;
    public byte[] getHinhAnh() { return HinhAnh; }
    public void setHinhAnh(byte[] hinhAnh) { HinhAnh = hinhAnh; }


    public int getMaMonAn() {
        return MaMonAn;
    }

    public void setMaMonAn(int maMonAn) {
        MaMonAn = maMonAn;
    }

    public int getMaLoai() {
        return MaLoai;
    }

    public void setMaLoai(int maLoai) {
        MaLoai = maLoai;
    }

    public String getTenMonAn() {
        return TenMonAn;
    }

    public void setTenMonAn(String tenMonAn) {
        TenMonAn = tenMonAn;
    }

    public int getGiaTien() {
        return GiaTien;
    }

    public void setGiaTien(int giaTien) {
        GiaTien = giaTien;
    }
}
