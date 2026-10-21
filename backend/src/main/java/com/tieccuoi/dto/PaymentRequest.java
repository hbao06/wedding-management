package com.tieccuoi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(@NotBlank String maPhieu, @NotNull LocalDate ngayThanhToan,
        @NotNull @DecimalMin("0") BigDecimal soTienCanThu) {}
