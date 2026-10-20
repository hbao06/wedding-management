package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "KHACHHANG")
@PrimaryKeyJoinColumn(name = "MaKH", referencedColumnName = "MaND")
public class KhachHang extends NguoiDung {
    @Column(name = "HoTenChuRe", nullable = false, length = 100)
    private String hoTenChuRe;

    @Column(name = "HoTenCoDau", nullable = false, length = 100)
    private String hoTenCoDau;

    @Column(name = "DiaChi", length = 200)
    private String diaChi;

    protected KhachHang() {}
}
