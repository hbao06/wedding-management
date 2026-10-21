package com.tieccuoi.controller;

import com.tieccuoi.dto.PaymentBookingResponse;
import com.tieccuoi.dto.PaymentPreviewResponse;
import com.tieccuoi.dto.PaymentRequest;
import com.tieccuoi.model.NhanVien;
import com.tieccuoi.repository.NhanVienRepository;
import com.tieccuoi.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/thanh-toan")
public class PaymentController {
    private final PaymentService paymentService;
    private final NhanVienRepository nhanVienRepository;

    public PaymentController(PaymentService paymentService, NhanVienRepository nhanVienRepository) {
        this.paymentService = paymentService;
        this.nhanVienRepository = nhanVienRepository;
    }

    @GetMapping("/phieu")
    public List<PaymentBookingResponse> search(@RequestParam(defaultValue = "") String q,
            HttpSession session) {
        accountant(session);
        return paymentService.search(q);
    }

    @GetMapping("/phieu/{maPhieu}/xem-truoc")
    public PaymentPreviewResponse preview(@PathVariable String maPhieu,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayThanhToan,
            HttpSession session) {
        accountant(session);
        return paymentService.preview(maPhieu, ngayThanhToan);
    }

    @PostMapping("/hoa-don")
    public PaymentPreviewResponse confirm(@Valid @RequestBody PaymentRequest request,
            HttpSession session) {
        NhanVien accountant = accountant(session);
        return paymentService.confirm(request.maPhieu(), request.ngayThanhToan(),
                request.soTienCanThu(), accountant.getMaND());
    }

    private NhanVien accountant(HttpSession session) {
        Object id = session.getAttribute("maND");
        if (!(id instanceof String)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cần đăng nhập");
        }
        NhanVien user = nhanVienRepository.findById((String) id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        if (!Boolean.TRUE.equals(user.getTrangThai()) || !"Kế toán".equals(user.getChucVu())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ kế toán được thanh toán");
        }
        return user;
    }
}
