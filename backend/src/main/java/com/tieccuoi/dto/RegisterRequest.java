package com.tieccuoi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String hoTen,
        @NotBlank @Size(min = 4, max = 50) String tenDangNhap,
        @NotBlank @Size(min = 8, max = 72) String matKhau,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 15) String sdt,
        @NotBlank @Size(max = 100) String hoTenChuRe,
        @NotBlank @Size(max = 100) String hoTenCoDau
) {}
