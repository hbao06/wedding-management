
package com.tieccuoi.controller;

import com.tieccuoi.dto.LoginRequest;
import com.tieccuoi.dto.LoginResponse;
import com.tieccuoi.dto.RegisterRequest;
import com.tieccuoi.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            AuthService authService,
            AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository =
                new HttpSessionSecurityContextRepository();
    }

    // Dang nhap
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        // Xac thuc tai khoan va mat khau
        Authentication authentication =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.tenDangNhap(),
                        request.matKhau()
                    )
                );

        // Lay thong tin nguoi dung
        LoginResponse response = authService.login(request);

        // Tao SecurityContext cho nguoi dung
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Luu trang thai dang nhap vao session
        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return response;
    }

    // Dang ky tai khoan khach hang
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.registerCustomer(request);
    }

    // Dang xuat
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
    }
}
