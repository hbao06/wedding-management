package com.tieccuoi.repository;

import com.tieccuoi.model.PhieuDatTiecCuoi;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhieuDatTiecCuoiRepository extends JpaRepository<PhieuDatTiecCuoi, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PhieuDatTiecCuoi p where p.maPhieu = :maPhieu")
    Optional<PhieuDatTiecCuoi> findForPayment(@Param("maPhieu") String maPhieu);

    @Query("select p from PhieuDatTiecCuoi p where :q = '' or lower(p.maPhieu) like lower(concat('%', :q, '%')) "
        + "or lower(p.tenChuRe) like lower(concat('%', :q, '%')) "
        + "or lower(p.tenCoDau) like lower(concat('%', :q, '%'))")
    Page<PhieuDatTiecCuoi> searchForPayment(@Param("q") String q, Pageable pageable);
}
