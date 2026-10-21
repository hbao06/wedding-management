
package com.tieccuoi.controller;

import com.tieccuoi.dto.LoginResponse;
import com.tieccuoi.dto.RegisterRequest;
import com.tieccuoi.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AuthService authService;

    public AdminUserController(AuthService authService) {
        this.authService = authService;
    }

    // Admin tao tai khoan nhan vien
    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse createEmployee(
            @Valid @RequestBody RegisterRequest request,
            HttpSession session) {

        Object vaiTro = session.getAttribute("vaiTro");

        if (!"Admin".equals(vaiTro)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Ban khong co quyen thuc hien thao tac nay"
            );
        }

        return authService.createEmployee(request);
    }
}
