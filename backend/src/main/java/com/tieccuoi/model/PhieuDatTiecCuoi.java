package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PHIEUDATTIECCUOI")
public class PhieuDatTiecCuoi {
    @Id
    @Column(name = "MaPhieu", columnDefinition = "char(10)")
    private String maPhieu;

    @Column(name = "MaKH", nullable = false, columnDefinition = "char(10)")
    private String maKH;

    @Column(name = "MaSanh", nullable = false, columnDefinition = "char(10)")
    private String maSanh;

    @Column(name = "MaNV", nullable = false, columnDefinition = "char(10)")
    private String maNV;

    @Column(name = "TenChuRe", nullable = false, length = 100)
    private String tenChuRe;

    @Column(name = "TenCoDau", nullable = false, length = 100)
    private String tenCoDau;

    @Column(name = "NgayDaiTiec", nullable = false)
    private LocalDate ngayDaiTiec;

    @Column(name = "Ca", nullable = false, length = 10)
    private String ca;

    @Column(name = "SoLuongBan", nullable = false)
    private Integer soLuongBan;

    @Column(name = "DonGiaBan", nullable = false, precision = 15, scale = 0)
    private BigDecimal donGiaBan;

    @Column(name = "TienDatCoc", nullable = false, precision = 15, scale = 0)
    private BigDecimal tienDatCoc;

    @Column(name = "TongTienDuKien", nullable = false, precision = 15, scale = 0)
    private BigDecimal tongTienDuKien;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;

    @Column(name = "NgayLapPhieu", nullable = false)
    private LocalDateTime ngayLapPhieu;

    protected PhieuDatTiecCuoi() {}
}
