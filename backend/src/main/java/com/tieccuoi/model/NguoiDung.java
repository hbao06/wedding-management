package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "NGUOIDUNG")
@Inheritance(strategy = InheritanceType.JOINED)
public class NguoiDung {
    @Id
    @Column(name = "MaND", columnDefinition = "char(10)")
    private String maND;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "TenDangNhap", nullable = false, length = 50)
    private String tenDangNhap;

    @Column(name = "MatKhauHash", nullable = false, length = 255)
    private String matKhauHash;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "VaiTro", nullable = false, length = 20)
    private String vaiTro;

    @Column(name = "TrangThai", nullable = false)
    private Boolean trangThai;

    protected NguoiDung() {}
    
        public String getMaND() {
            return maND;
        }

        public String getHoTen() {
            return hoTen;
        }

        public String getTenDangNhap() {
            return tenDangNhap;
        }

        public String getMatKhauHash() {
            return matKhauHash;
        }

        public String getVaiTro() {
            return vaiTro;
        }

        public Boolean getTrangThai() {
            return trangThai;
        }

}
