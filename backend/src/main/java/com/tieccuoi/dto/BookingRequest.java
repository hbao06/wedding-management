package com.tieccuoi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingRequest(
    String maKH,
    String maNV,
    String maSanh,
    String tenChuRe,
    String tenCoDau,
    LocalDate ngayDaiTiec,
    String ca,
    Integer soLuongBan,
    BigDecimal donGiaBan,
    BigDecimal tienDatCoc
) {}
