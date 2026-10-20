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
}
