package com.tieccuoi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentBookingResponse(String maPhieu, String tenChuRe, String tenCoDau,
        LocalDate ngayDaiTiec, BigDecimal tongTienHoaDon, BigDecimal tienDatCoc,
        boolean daThanhToan) {}
