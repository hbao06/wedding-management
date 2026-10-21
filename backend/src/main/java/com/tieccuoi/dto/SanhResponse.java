package com.tieccuoi.dto;

import com.tieccuoi.model.Sanh;
import java.math.BigDecimal;

public record SanhResponse(
    String maSanh,
    String tenSanh,
    String loaiSanh,
    Integer soBanToiDa,
    BigDecimal donGiaToiThieu
) {
    public static SanhResponse from(Sanh sanh) {
        return new SanhResponse(
            sanh.getMaSanh(),
            sanh.getTenSanh(),
            sanh.getLoaiSanh(),
            sanh.getSoBanToiDa(),
            sanh.getDonGiaToiThieu()
        );
    }
}
