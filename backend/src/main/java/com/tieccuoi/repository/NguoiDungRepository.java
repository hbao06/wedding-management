
package com.tieccuoi.repository;

import com.tieccuoi.model.NguoiDung;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguoiDungRepository
        extends JpaRepository<NguoiDung, String> {

    // Tim nguoi dung theo ten dang nhap
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    // Kiem tra ten dang nhap da ton tai chua
    boolean existsByTenDangNhap(String tenDangNhap);
}
