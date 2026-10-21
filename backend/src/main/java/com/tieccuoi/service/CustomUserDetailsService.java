package com.tieccuoi.service;

import com.tieccuoi.model.NguoiDung;
import com.tieccuoi.repository.NguoiDungRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final NguoiDungRepository users;

    public CustomUserDetailsService(NguoiDungRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        NguoiDung user = users.findByTenDangNhap(username)
            .orElseThrow(() -> new UsernameNotFoundException("Unknown account"));
        return User.withUsername(user.getTenDangNhap())
            .password(user.getMatKhauHash())
            .roles(user.getVaiTro())
            .disabled(!Boolean.TRUE.equals(user.getTrangThai()))
            .build();
    }
}
