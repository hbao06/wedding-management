package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "HOADONTHANHTOAN")
public class HoaDonThanhToan {
    @Id
    @Column(name = "MaHD", columnDefinition = "char(10)")
    private String maHD;

    @Column(name = "MaPhieu", nullable = false, columnDefinition = "char(10)")
    private String maPhieu;

    @Column(name = "MaNV", nullable = false, columnDefinition = "char(10)")
    private String maNV;

    @Column(name = "NgayLapHD", nullable = false)
    private LocalDate ngayLapHD;

    @Column(name = "NgayThanhToan", nullable = false)
    private LocalDate ngayThanhToan;

    @Column(name = "TongTienHoaDon", nullable = false, precision = 15, scale = 0)
    private BigDecimal tongTienHoaDon;

    @Column(name = "TienPhatTreHan", nullable = false, precision = 15, scale = 0)
    private BigDecimal tienPhatTreHan;

    @Column(name = "SoTienConLai", nullable = false, precision = 15, scale = 0)
    private BigDecimal soTienConLai;

    @Column(name = "TrangThaiTT", nullable = false, length = 20)
    private String trangThaiTT;

    protected HoaDonThanhToan() {}

    public HoaDonThanhToan(String maHD, String maPhieu, String maNV,
            LocalDate ngayLapHD, LocalDate ngayThanhToan, BigDecimal tongTienHoaDon,
            BigDecimal tienPhatTreHan) {
        this.maHD = maHD;
        this.maPhieu = maPhieu;
        this.maNV = maNV;
        this.ngayLapHD = ngayLapHD;
        this.ngayThanhToan = ngayThanhToan;
        this.tongTienHoaDon = tongTienHoaDon;
        this.tienPhatTreHan = tienPhatTreHan;
        this.soTienConLai = BigDecimal.ZERO;
        this.trangThaiTT = "Đã thanh toán";
    }

    public String getMaHD() { return maHD; }
    public LocalDate getNgayThanhToan() { return ngayThanhToan; }
    public BigDecimal getTongTienHoaDon() { return tongTienHoaDon; }
    public BigDecimal getTienPhatTreHan() { return tienPhatTreHan; }
    public BigDecimal getSoTienConLai() { return soTienConLai; }
}
