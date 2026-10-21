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

    public String getChucVu() { return chucVu; }
}
