package com.example.anhki.foodapp.DTO;

public class ChiTietGoiMonDTO {
    // Các tên biến này phải khớp với tên trường trong sub-collection "chiTietGoiMon" trên Firestore
    private String tenMonAn;
    private long giaTien;
    private long soLuong;
    private String documentId;

    // Getters and Setters
    public String getTenMonAn() {
        return tenMonAn;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public void setTenMonAn(String tenMonAn) {
        this.tenMonAn = tenMonAn;
    }

    public long getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(long giaTien) {
        this.giaTien = giaTien;
    }

    public long getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(long soLuong) {
        this.soLuong = soLuong;
    }
}
