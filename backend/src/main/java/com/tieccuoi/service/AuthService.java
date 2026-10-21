
package com.tieccuoi.service;

import com.tieccuoi.dto.LoginRequest;
import com.tieccuoi.dto.LoginResponse;
import com.tieccuoi.dto.RegisterRequest;
import com.tieccuoi.model.NguoiDung;
import com.tieccuoi.repository.NguoiDungRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    // Dang nhap
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

    // Dang ky khach hang
    @Transactional
    public LoginResponse registerCustomer(RegisterRequest request) {
        return createAccount(request, "KhachHang");
    }

    // Tao tai khoan nhan vien
    @Transactional
    public LoginResponse createEmployee(RegisterRequest request) {
        return createAccount(request, "NhanVien");
    }

    // Tao tai khoan voi vai tro duoc quy dinh boi backend
    private LoginResponse createAccount(
            RegisterRequest request, String vaiTro) {

        if (nguoiDungRepository.existsByTenDangNhap(
                request.tenDangNhap())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ten dang nhap da ton tai"
            );
        }

        NguoiDung nguoiDung = new NguoiDung();

        nguoiDung.setMaND(
            "ND" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase()
        );

        nguoiDung.setHoTen(request.hoTen());
        nguoiDung.setTenDangNhap(request.tenDangNhap());
        nguoiDung.setMatKhauHash(
            passwordEncoder.encode(request.matKhau())
        );
        nguoiDung.setSdt(request.sdt());
        nguoiDung.setEmail(request.email());
        nguoiDung.setVaiTro(vaiTro);
        nguoiDung.setTrangThai(true);

        nguoiDungRepository.save(nguoiDung);

        return new LoginResponse(
            nguoiDung.getMaND(),
            nguoiDung.getHoTen(),
            nguoiDung.getTenDangNhap(),
            nguoiDung.getVaiTro()
        );
    }

    // Loi dang nhap
    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Ten dang nhap hoac mat khau khong dung"
        );
    }
}
