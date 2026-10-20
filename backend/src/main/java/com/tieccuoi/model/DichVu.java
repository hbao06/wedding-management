package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "DICHVU")
public class DichVu {
    @Id
    @Column(name = "MaDV", columnDefinition = "char(10)")
    private String maDV;

    @Column(name = "TenDichVu", nullable = false, length = 100)
    private String tenDichVu;

    @Column(name = "DonGia", nullable = false, precision = 15, scale = 0)
    private BigDecimal donGia;

    @Column(name = "DonViTinh", nullable = false, length = 20)
    private String donViTinh;

    protected DichVu() {}
}
