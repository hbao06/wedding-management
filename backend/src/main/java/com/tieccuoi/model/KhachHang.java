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

    public static KhachHang register(String maKH, String hoTen, String tenDangNhap,
            String matKhauHash, String email, String sdt, String tenChuRe, String tenCoDau) {
        KhachHang customer = new KhachHang();
        customer.setMaND(maKH);
        customer.setHoTen(hoTen);
        customer.setTenDangNhap(tenDangNhap);
        customer.setMatKhauHash(matKhauHash);
        customer.setEmail(email);
        customer.setSdt(sdt);
        customer.setVaiTro("KhachHang");
        customer.setTrangThai(true);
        customer.hoTenChuRe = tenChuRe;
        customer.hoTenCoDau = tenCoDau;
        return customer;
    }
}
