
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

    private final NguoiDungRepository nguoiDungRepository;

    public CustomUserDetailsService(
            NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String tenDangNhap)
            throws UsernameNotFoundException {

        NguoiDung nguoiDung = nguoiDungRepository
            .findByTenDangNhap(tenDangNhap)
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "Khong tim thay tai khoan"
                )
            );

        if (!Boolean.TRUE.equals(nguoiDung.getTrangThai())) {
            throw new UsernameNotFoundException(
                "Tai khoan khong hoat dong"
            );
        }

        return User.withUsername(nguoiDung.getTenDangNhap())
            .password(nguoiDung.getMatKhauHash())
            .authorities("ROLE_" + nguoiDung.getVaiTro())
            .build();
    }
}
