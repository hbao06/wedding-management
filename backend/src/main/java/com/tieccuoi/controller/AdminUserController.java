package com.tieccuoi.controller;

import com.tieccuoi.dto.EmployeeRegisterRequest;
import com.tieccuoi.dto.LoginResponse;
import com.tieccuoi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AuthService authService;

    public AdminUserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse createEmployee(@Valid @RequestBody EmployeeRegisterRequest request) {
        return authService.createEmployee(request);
    }
}
