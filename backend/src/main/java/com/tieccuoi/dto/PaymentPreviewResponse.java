package com.tieccuoi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentPreviewResponse(String maPhieu, String tenChuRe, String tenCoDau,
        LocalDate ngayDaiTiec, LocalDate ngayThanhToan, BigDecimal tongTienHoaDon,
        BigDecimal tienDatCoc, long soNgayTre, BigDecimal tyLePhatMoiNgay,
        BigDecimal tienPhatTreHan, BigDecimal soTienCanThu, boolean daThanhToan,
        String maHD) {}
