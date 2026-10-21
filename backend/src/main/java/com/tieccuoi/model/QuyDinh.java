package com.tieccuoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "QUYDINH")
public class QuyDinh {
    @Id
    @Column(name = "MaQD", columnDefinition = "char(10)")
    private String maQD;

    @Column(name = "TenQuyDinh", nullable = false, length = 150)
    private String tenQuyDinh;

    @Column(name = "GiaTriQuyDinh", nullable = false, length = 200)
    private String giaTriQuyDinh;

    @Column(name = "NgayApDung", nullable = false)
    private LocalDate ngayApDung;

    protected QuyDinh() {}

    public String getGiaTriQuyDinh() { return giaTriQuyDinh; }
    public LocalDate getNgayApDung() { return ngayApDung; }
}
