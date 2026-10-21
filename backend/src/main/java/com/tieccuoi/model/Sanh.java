package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "SANH")
public class Sanh {
    @Id
    @Column(name = "MaSanh", columnDefinition = "char(10)")
    private String maSanh;

    @Column(name = "TenSanh", nullable = false, length = 100)
    private String tenSanh;

    @Column(name = "LoaiSanh", nullable = false, length = 20)
    private String loaiSanh;

    @Column(name = "SoBanToiDa", nullable = false)
    private Integer soBanToiDa;

    @Column(name = "DonGiaToiThieu", nullable = false, precision = 15, scale = 0)
    private BigDecimal donGiaToiThieu;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;

    protected Sanh() {}

    public String getMaSanh() {
        return maSanh;
    }

    public String getTenSanh() {
        return tenSanh;
    }

    public String getLoaiSanh() {
        return loaiSanh;
    }

    public Integer getSoBanToiDa() {
        return soBanToiDa;
    }

    public BigDecimal getDonGiaToiThieu() {
        return donGiaToiThieu;
    }

    public String getTrangThai() {
        return trangThai;
    }
}
