package com.example.anhki.foodapp.DTO;

public class BanAnDTO {
    private String documentId;
    private int MaBan;
    private String TenBan;
    private boolean DuocChon;

    public boolean isDuocChon() {
        return DuocChon;
    }

    public void setDuocChon(boolean duocChon) {
        DuocChon = duocChon;
    }

    public int getMaBan() {
        return MaBan;
    }

    public void setMaBan(int maBan) {
        MaBan = maBan;
    }

    public String getTenBan() {
        return TenBan;
    }

    public void setTenBan(String tenBan) {
        TenBan = tenBan;
    }
    //update để đưa phuong thuc xu lý ve fragment
    private String TinhTrang;
    public String getTinhTrang() { return TinhTrang; }
    public void setTinhTrang(String tinhTrang) { TinhTrang = tinhTrang; }
    // THÊM GETTER/SETTER NÀY
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
 }
