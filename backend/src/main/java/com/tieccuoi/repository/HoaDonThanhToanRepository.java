package com.tieccuoi.repository;

import com.tieccuoi.model.HoaDonThanhToan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoaDonThanhToanRepository extends JpaRepository<HoaDonThanhToan, String> {
    boolean existsByMaPhieu(String maPhieu);
    Optional<HoaDonThanhToan> findByMaPhieu(String maPhieu);
}
