package com.example.anhki.foodapp.DTO;

import com.google.firebase.firestore.DocumentReference;

public class MonAnDTO {
    private int MaMonAn;
    private String TenMonAn;
    private int GiaTien;
    private String documentId;
    private DocumentReference maLoaiRef; // <-- THÊM TRƯỜNG NÀY

    // Trong MonAnDTO.java
    private String HinhAnh;
    public String getHinhAnh() { return HinhAnh; }
    public void setHinhAnh(String hinhAnh) { HinhAnh = hinhAnh; }

    public DocumentReference getMaLoaiRef() {
        return maLoaiRef;
    }

    public void setMaLoaiRef(DocumentReference maLoaiRef) {
        this.maLoaiRef = maLoaiRef;
    }

    public int getMaMonAn() {
        return MaMonAn;
    }

    public void setMaMonAn(int maMonAn) {
        MaMonAn = maMonAn;
    }

    //public int getMaLoai() {
//        return MaLoai;
//    }
//
//    public void setMaLoai(int maLoai) {
//        MaLoai = maLoai;
//    }

    public String getTenMonAn() {
        return TenMonAn;
    }

    public void setTenMonAn(String tenMonAn) {
        TenMonAn = tenMonAn;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getGiaTien() {
        return GiaTien;
    }

    public void setGiaTien(int giaTien) {
        GiaTien = giaTien;
    }
}
