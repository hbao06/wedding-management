package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "NHANVIEN")
@PrimaryKeyJoinColumn(name = "MaNV", referencedColumnName = "MaND")
public class NhanVien extends NguoiDung {
    @Column(name = "ChucVu", nullable = false, length = 30)
    private String chucVu;

    @Column(name = "NgayVaoLam", nullable = false)
    private LocalDate ngayVaoLam;

    protected NhanVien() {}

    public static NhanVien register(String maNV, String hoTen, String tenDangNhap,
            String matKhauHash, String email, String sdt, String chucVu, LocalDate ngayVaoLam) {
        NhanVien employee = new NhanVien();
        employee.setMaND(maNV);
        employee.setHoTen(hoTen);
        employee.setTenDangNhap(tenDangNhap);
        employee.setMatKhauHash(matKhauHash);
        employee.setEmail(email);
        employee.setSdt(sdt);
        employee.setVaiTro("NhanVien");
        employee.setTrangThai(true);
        employee.chucVu = chucVu;
        employee.ngayVaoLam = ngayVaoLam;
        return employee;
    }

    public String getChucVu() { return chucVu; }
}
