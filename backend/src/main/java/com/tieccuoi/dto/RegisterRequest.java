package com.tieccuoi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    String hoTen,

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 50)
    String tenDangNhap,

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 72)
    String matKhau,

    @Size(max = 15)
    String sdt,

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    String email
) {}