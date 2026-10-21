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

    public PhieuDatTiecCuoi(
            String maPhieu,
            String maKH,
            String maSanh,
            String maNV,
            String tenChuRe,
            String tenCoDau,
            LocalDate ngayDaiTiec,
            String ca,
            Integer soLuongBan,
            BigDecimal donGiaBan,
            BigDecimal tienDatCoc,
            BigDecimal tongTienDuKien,
            String trangThai,
            LocalDateTime ngayLapPhieu) {
        this.maPhieu = maPhieu;
        this.maKH = maKH;
        this.maSanh = maSanh;
        this.maNV = maNV;
        this.tenChuRe = tenChuRe;
        this.tenCoDau = tenCoDau;
        this.ngayDaiTiec = ngayDaiTiec;
        this.ca = ca;
        this.soLuongBan = soLuongBan;
        this.donGiaBan = donGiaBan;
        this.tienDatCoc = tienDatCoc;
        this.tongTienDuKien = tongTienDuKien;
        this.trangThai = trangThai;
        this.ngayLapPhieu = ngayLapPhieu;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getMaSanh() {
        return maSanh;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getTenChuRe() {
        return tenChuRe;
    }

    public String getTenCoDau() {
        return tenCoDau;
    }

    public LocalDate getNgayDaiTiec() {
        return ngayDaiTiec;
    }

    public String getCa() {
        return ca;
    }

    public Integer getSoLuongBan() {
        return soLuongBan;
    }

    public BigDecimal getDonGiaBan() {
        return donGiaBan;
    }

    public BigDecimal getTienDatCoc() {
        return tienDatCoc;
    }

    public BigDecimal getTongTienDuKien() {
        return tongTienDuKien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public LocalDateTime getNgayLapPhieu() {
        return ngayLapPhieu;
    }
}
