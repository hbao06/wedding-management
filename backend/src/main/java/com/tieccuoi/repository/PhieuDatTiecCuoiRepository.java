package com.tieccuoi.repository;

import com.tieccuoi.model.PhieuDatTiecCuoi;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhieuDatTiecCuoiRepository extends JpaRepository<PhieuDatTiecCuoi, String> {

    boolean existsByMaSanhAndNgayDaiTiecAndCa(String maSanh, LocalDate ngayDaiTiec, String ca);

    Optional<PhieuDatTiecCuoi> findTopByOrderByMaPhieuDesc();
}
