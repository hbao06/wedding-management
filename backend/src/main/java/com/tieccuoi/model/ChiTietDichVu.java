package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "CHITIETDICHVU")
public class ChiTietDichVu {
    @Id
    @Column(name = "MaCTDV", columnDefinition = "char(10)")
    private String maCTDV;

    @Column(name = "MaPhieu", nullable = false, columnDefinition = "char(10)")
    private String maPhieu;

    @Column(name = "MaDV", nullable = false, columnDefinition = "char(10)")
    private String maDV;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "ThanhTien", nullable = false, precision = 15, scale = 0)
    private BigDecimal thanhTien;

    protected ChiTietDichVu() {}
}
