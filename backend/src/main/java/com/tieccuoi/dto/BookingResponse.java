package com.tieccuoi.dto;

import com.tieccuoi.model.PhieuDatTiecCuoi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
    String maPhieu,
    String maKH,
    String maNV,
    String maSanh,
    String tenSanh,
    String tenChuRe,
    String tenCoDau,
    LocalDate ngayDaiTiec,
    String ca,
    Integer soLuongBan,
    BigDecimal donGiaBan,
    BigDecimal tienDatCoc,
    BigDecimal tongTienDuKien,
    BigDecimal soTienConLai,
    String trangThai,
    LocalDateTime ngayLapPhieu
) {
    public static BookingResponse from(PhieuDatTiecCuoi phieu, String tenSanh) {
        return new BookingResponse(
            phieu.getMaPhieu(),
            phieu.getMaKH(),
            phieu.getMaNV(),
            phieu.getMaSanh(),
            tenSanh,
            phieu.getTenChuRe(),
            phieu.getTenCoDau(),
            phieu.getNgayDaiTiec(),
            phieu.getCa(),
            phieu.getSoLuongBan(),
            phieu.getDonGiaBan(),
            phieu.getTienDatCoc(),
            phieu.getTongTienDuKien(),
            phieu.getTongTienDuKien().subtract(phieu.getTienDatCoc()),
            phieu.getTrangThai(),
            phieu.getNgayLapPhieu()
        );
    }
}
