
package com.tieccuoi.service;

import com.tieccuoi.dto.LoginRequest;
import com.tieccuoi.dto.LoginResponse;
import com.tieccuoi.model.NguoiDung;
import com.tieccuoi.repository.NguoiDungRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            NguoiDungRepository nguoiDungRepository,
            PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository
            .findByTenDangNhap(request.tenDangNhap())
            .orElseThrow(this::invalidCredentials);

        if (nguoiDung.getTrangThai() == null
                || !nguoiDung.getTrangThai()
                || !passwordEncoder.matches(
                    request.matKhau(),
                    nguoiDung.getMatKhauHash())) {
            throw invalidCredentials();
        }

        return new LoginResponse(
            nguoiDung.getMaND(),
            nguoiDung.getHoTen(),
            nguoiDung.getTenDangNhap(),
            nguoiDung.getVaiTro()
        );
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Tên đăng nhập hoặc mật khẩu không đúng"
        );
    }
}
