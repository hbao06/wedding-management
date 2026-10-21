package com.tieccuoi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tieccuoi.dto.LoginResponse;
import com.tieccuoi.dto.RegisterRequest;
import com.tieccuoi.model.KhachHang;
import com.tieccuoi.model.NguoiDung;
import com.tieccuoi.repository.KhachHangRepository;
import com.tieccuoi.repository.NguoiDungRepository;
import com.tieccuoi.repository.NhanVienRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceTest {
    private final NguoiDungRepository users = mock(NguoiDungRepository.class);
    private final KhachHangRepository customers = mock(KhachHangRepository.class);
    private final NhanVienRepository employees = mock(NhanVienRepository.class);
    private final PasswordEncoder passwords = mock(PasswordEncoder.class);
    private final AuthService service = new AuthService(users, customers, employees, passwords);

    private RegisterRequest request() {
        return new RegisterRequest("Lan Anh", "lananh", "password123", "lan@example.com",
            "0123456789", "Minh Anh", "Lan Anh");
    }

    @Test
    void registrationCreatesCustomerWithHashedPasswordAndBookingIdentity() {
        when(passwords.encode("password123")).thenReturn("bcrypt-hash");

        LoginResponse result = service.registerCustomer(request());

        ArgumentCaptor<KhachHang> saved = ArgumentCaptor.forClass(KhachHang.class);
        verify(customers).saveAndFlush(saved.capture());
        assertEquals(result.maND(), saved.getValue().getMaND());
        assertEquals(10, result.maND().length());
        assertEquals("KhachHang", saved.getValue().getVaiTro());
        assertEquals("bcrypt-hash", saved.getValue().getMatKhauHash());
        assertEquals("lananh", result.tenDangNhap());
    }

    @Test
    void duplicateUsernameReturnsConflict() {
        when(users.existsByTenDangNhap("lananh")).thenReturn(true);

        assertEquals(409, assertThrows(ResponseStatusException.class,
            () -> service.registerCustomer(request())).getStatusCode().value());
    }

    @Test
    void disabledAccountCannotBeLoadedForAuthentication() {
        NguoiDung user = mock(NguoiDung.class);
        when(user.getTenDangNhap()).thenReturn("disabled");
        when(user.getMatKhauHash()).thenReturn("hash");
        when(user.getVaiTro()).thenReturn("KhachHang");
        when(user.getTrangThai()).thenReturn(false);
        when(users.findByTenDangNhap("disabled")).thenReturn(Optional.of(user));

        assertEquals(false, new CustomUserDetailsService(users)
            .loadUserByUsername("disabled").isEnabled());
    }
}
