
package com.tieccuoi.service;

import com.tieccuoi.dto.LoginRequest;
import com.tieccuoi.dto.LoginResponse;
import com.tieccuoi.dto.EmployeeRegisterRequest;
import com.tieccuoi.dto.RegisterRequest;
import com.tieccuoi.model.KhachHang;
import com.tieccuoi.model.NguoiDung;
import com.tieccuoi.model.NhanVien;
import com.tieccuoi.repository.KhachHangRepository;
import com.tieccuoi.repository.NguoiDungRepository;
import com.tieccuoi.repository.NhanVienRepository;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            NguoiDungRepository nguoiDungRepository,
            KhachHangRepository khachHangRepository,
            NhanVienRepository nhanVienRepository,
            PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
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
            nguoiDung.getVaiTro(),
            nguoiDung instanceof NhanVien nhanVien ? nhanVien.getChucVu() : null
        );
    }

    public LoginResponse currentUser(String maND) {
        NguoiDung user = nguoiDungRepository.findById(maND)
            .orElseThrow(this::invalidCredentials);
        if (!Boolean.TRUE.equals(user.getTrangThai())) {
            throw invalidCredentials();
        }
        return new LoginResponse(user.getMaND(), user.getHoTen(),
                user.getTenDangNhap(), user.getVaiTro(),
                user instanceof NhanVien nhanVien ? nhanVien.getChucVu() : null);
    }

    @Transactional
    public LoginResponse registerCustomer(RegisterRequest request) {
        String username = request.tenDangNhap().trim();
        if (nguoiDungRepository.existsByTenDangNhap(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại");
        }
        String id = "ND" + UUID.randomUUID().toString().replace("-", "")
            .substring(0, 8).toUpperCase();
        KhachHang customer = KhachHang.register(id, request.hoTen().trim(), username,
            passwordEncoder.encode(request.matKhau()), request.email().trim(),
            request.sdt() == null ? null : request.sdt().trim(),
            request.hoTenChuRe().trim(), request.hoTenCoDau().trim());
        try {
            khachHangRepository.saveAndFlush(customer);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Tên đăng nhập đã tồn tại; vui lòng thử lại", exception);
        }
        return new LoginResponse(id, customer.getHoTen(), username, "KhachHang", null);
    }

    @Transactional
    public LoginResponse createEmployee(EmployeeRegisterRequest request) {
        String username = request.tenDangNhap().trim();
        if (nguoiDungRepository.existsByTenDangNhap(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại");
        }
        String id = "NV" + UUID.randomUUID().toString().replace("-", "")
            .substring(0, 8).toUpperCase();
        NhanVien employee = NhanVien.register(id, request.hoTen().trim(), username,
            passwordEncoder.encode(request.matKhau()), request.email().trim(),
            request.sdt() == null ? null : request.sdt().trim(),
            request.chucVu(), request.ngayVaoLam());
        try {
            nhanVienRepository.saveAndFlush(employee);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Tên đăng nhập đã tồn tại; vui lòng thử lại", exception);
        }
        return new LoginResponse(id, employee.getHoTen(), username, "NhanVien",
            employee.getChucVu());
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Tên đăng nhập hoặc mật khẩu không đúng"
        );
    }
}
