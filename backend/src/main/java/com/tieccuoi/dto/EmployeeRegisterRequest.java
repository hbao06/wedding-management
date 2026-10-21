package com.tieccuoi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EmployeeRegisterRequest(
        @NotBlank @Size(max = 100) String hoTen,
        @NotBlank @Size(min = 4, max = 50) String tenDangNhap,
        @NotBlank @Size(min = 8, max = 72) String matKhau,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 15) String sdt,
        @NotBlank @Pattern(regexp = "Sales|Kế toán|Quản lý") String chucVu,
        @NotNull LocalDate ngayVaoLam
) {}
