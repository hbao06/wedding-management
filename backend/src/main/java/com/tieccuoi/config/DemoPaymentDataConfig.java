package com.tieccuoi.config;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("demo")
public class DemoPaymentDataConfig {
    @Bean
    CommandLineRunner paymentDemoData(JdbcTemplate jdbc, PasswordEncoder passwords,
            @Value("${DEMO_PAYMENT_PASSWORD:}") String demoPassword) {
        return args -> {
            if (demoPassword.isBlank()) {
                throw new IllegalStateException("Set DEMO_PAYMENT_PASSWORD when using the demo profile");
            }
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            insertUser(jdbc, passwords, "DMKT000001", "Kế toán Demo", "demo_ketoan",
                    demoPassword, "NhanVien");
            insertUser(jdbc, passwords, "DMSA000001", "Sales Demo", "demo_sales",
                    UUID.randomUUID().toString(), "NhanVien");
            insertUser(jdbc, passwords, "DMKH000001", "Khách hàng Demo", "demo_khach",
                    UUID.randomUUID().toString(), "KhachHang");

            if (!exists(jdbc, "NHANVIEN", "MaNV", "DMKT000001")) {
                jdbc.update("INSERT INTO NHANVIEN (MaNV, ChucVu, NgayVaoLam) VALUES (?, ?, ?)",
                        "DMKT000001", "Kế toán", today.minusYears(1));
            }
            if (!exists(jdbc, "NHANVIEN", "MaNV", "DMSA000001")) {
                jdbc.update("INSERT INTO NHANVIEN (MaNV, ChucVu, NgayVaoLam) VALUES (?, ?, ?)",
                        "DMSA000001", "Sales", today.minusYears(1));
            }
            if (!exists(jdbc, "KHACHHANG", "MaKH", "DMKH000001")) {
                jdbc.update("INSERT INTO KHACHHANG (MaKH, HoTenChuRe, HoTenCoDau) VALUES (?, ?, ?)",
                        "DMKH000001", "Minh Anh", "Thu Hà");
            }
            if (!exists(jdbc, "SANH", "MaSanh", "DMSH000001")) {
                jdbc.update("INSERT INTO SANH (MaSanh, TenSanh, LoaiSanh, SoBanToiDa, DonGiaToiThieu) "
                        + "VALUES (?, ?, ?, ?, ?)", "DMSH000001", "Sảnh Demo Payment", "A", 20, 1000000);
            }
            insertBooking(jdbc, "DMPH000001", "Minh Anh", "Thu Hà", today.minusDays(2), "TOI");
            insertBooking(jdbc, "DMPH000002", "Minh Anh", "Thu Hà", today, "TRUA");
        };
    }

    private void insertUser(JdbcTemplate jdbc, PasswordEncoder passwords, String id,
            String name, String username, String password, String role) {
        if (!exists(jdbc, "NGUOIDUNG", "MaND", id)) {
            jdbc.update("INSERT INTO NGUOIDUNG (MaND, HoTen, TenDangNhap, MatKhauHash, VaiTro) "
                    + "VALUES (?, ?, ?, ?, ?)", id, name, username, passwords.encode(password), role);
        }
    }

    private void insertBooking(JdbcTemplate jdbc, String id, String groom, String bride,
            LocalDate date, String shift) {
        if (!exists(jdbc, "PHIEUDATTIECCUOI", "MaPhieu", id)) {
            jdbc.update("INSERT INTO PHIEUDATTIECCUOI "
                    + "(MaPhieu, MaKH, MaSanh, MaNV, TenChuRe, TenCoDau, NgayDaiTiec, Ca, "
                    + "SoLuongBan, DonGiaBan, TienDatCoc, TongTienDuKien, TrangThai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    id, "DMKH000001", "DMSH000001", "DMSA000001", groom, bride,
                    date, shift, 10, 2000000, 6000000, 20000000, "Đã đặt cọc");
        }
    }

    private boolean exists(JdbcTemplate jdbc, String table, String column, String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?", Integer.class, id);
        return count != null && count > 0;
    }
}
