package com.tieccuoi.service;

import com.tieccuoi.dto.PaymentBookingResponse;
import com.tieccuoi.dto.PaymentPreviewResponse;
import com.tieccuoi.model.HoaDonThanhToan;
import com.tieccuoi.model.PhieuDatTiecCuoi;
import com.tieccuoi.model.QuyDinh;
import com.tieccuoi.repository.HoaDonThanhToanRepository;
import com.tieccuoi.repository.PhieuDatTiecCuoiRepository;
import com.tieccuoi.repository.QuyDinhRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final String ID_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PhieuDatTiecCuoiRepository phieuRepository;
    private final HoaDonThanhToanRepository hoaDonRepository;
    private final QuyDinhRepository quyDinhRepository;

    public PaymentService(PhieuDatTiecCuoiRepository phieuRepository,
            HoaDonThanhToanRepository hoaDonRepository, QuyDinhRepository quyDinhRepository) {
        this.phieuRepository = phieuRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.quyDinhRepository = quyDinhRepository;
    }

    @Transactional(readOnly = true)
    public List<PaymentBookingResponse> search(String query) {
        String q = query == null ? "" : query.trim();
        if (q.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Từ khóa quá dài");
        }
        return phieuRepository.searchForPayment(q,
                PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "ngayDaiTiec")))
            .map(p -> new PaymentBookingResponse(p.getMaPhieu(), p.getTenChuRe(),
                    p.getTenCoDau(), p.getNgayDaiTiec(), p.getTongTienDuKien(),
                    p.getTienDatCoc(), hoaDonRepository.existsByMaPhieu(p.getMaPhieu())))
            .getContent();
    }

    @Transactional(readOnly = true)
    public PaymentPreviewResponse preview(String maPhieu, LocalDate ngayThanhToan) {
        PhieuDatTiecCuoi phieu = phieuRepository.findById(maPhieu)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu"));
        HoaDonThanhToan existing = hoaDonRepository.findByMaPhieu(maPhieu).orElse(null);
        if (existing != null) {
            return calculate(phieu, existing.getNgayThanhToan(), existing);
        }
        validatePaymentDate(phieu, ngayThanhToan);
        return calculate(phieu, ngayThanhToan, null);
    }

    @Transactional
    public PaymentPreviewResponse confirm(String maPhieu, LocalDate ngayThanhToan,
            BigDecimal expectedAmount, String maNV) {
        PhieuDatTiecCuoi phieu = phieuRepository.findForPayment(maPhieu)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu"));
        if (hoaDonRepository.existsByMaPhieu(maPhieu)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phiếu đã được thanh toán");
        }
        validatePaymentDate(phieu, ngayThanhToan);
        PaymentPreviewResponse preview = calculate(phieu, ngayThanhToan, null);
        if (expectedAmount == null || preview.soTienCanThu().compareTo(expectedAmount) != 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Số tiền đã thay đổi; vui lòng xem trước lại");
        }
        HoaDonThanhToan hoaDon = new HoaDonThanhToan(newInvoiceId(), maPhieu, maNV,
                LocalDate.now(BUSINESS_ZONE), ngayThanhToan, preview.tongTienHoaDon(),
                preview.tienPhatTreHan());
        hoaDonRepository.saveAndFlush(hoaDon);
        phieu.setTrangThai("Hoàn tất");
        return new PaymentPreviewResponse(preview.maPhieu(), preview.tenChuRe(),
                preview.tenCoDau(), preview.ngayDaiTiec(), preview.ngayThanhToan(),
                preview.tongTienHoaDon(), preview.tienDatCoc(), preview.soNgayTre(),
                preview.tyLePhatMoiNgay(), preview.tienPhatTreHan(), preview.soTienCanThu(),
                true, hoaDon.getMaHD());
    }

    private void validatePaymentDate(PhieuDatTiecCuoi phieu, LocalDate date) {
        if (!"Đã đặt cọc".equals(phieu.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Phiếu chưa ở trạng thái đã đặt cọc");
        }
        if (date == null || date.isBefore(phieu.getNgayDaiTiec())
                || date.isAfter(LocalDate.now(BUSINESS_ZONE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ngày thanh toán phải từ ngày đãi tiệc đến hôm nay");
        }
    }

    private PaymentPreviewResponse calculate(PhieuDatTiecCuoi phieu, LocalDate date,
            HoaDonThanhToan existing) {
        long daysLate = Math.max(0, ChronoUnit.DAYS.between(phieu.getNgayDaiTiec(), date));
        BigDecimal total = existing == null ? phieu.getTongTienDuKien() : existing.getTongTienHoaDon();
        BigDecimal rate = daysLate == 0 || total.signum() == 0 ? BigDecimal.ZERO
            : existing == null ? lateRate(date)
            : existing.getTienPhatTreHan().divide(total.multiply(BigDecimal.valueOf(daysLate)),
                    6, RoundingMode.HALF_UP);
        BigDecimal penalty = existing == null
            ? total.multiply(rate).multiply(BigDecimal.valueOf(daysLate)).setScale(0, RoundingMode.HALF_UP)
            : existing.getTienPhatTreHan();
        BigDecimal due = total.add(penalty).subtract(phieu.getTienDatCoc()).max(BigDecimal.ZERO);
        return new PaymentPreviewResponse(phieu.getMaPhieu(), phieu.getTenChuRe(),
                phieu.getTenCoDau(), phieu.getNgayDaiTiec(), date, total,
                phieu.getTienDatCoc(), daysLate, rate, penalty, due,
                existing != null, existing == null ? null : existing.getMaHD());
    }

    private BigDecimal lateRate(LocalDate date) {
        QuyDinh enabled = policy("QD003", date);
        if (!"true".equalsIgnoreCase(enabled.getGiaTriQuyDinh().trim())) {
            return BigDecimal.ZERO;
        }
        String value = policy("QD002", date).getGiaTriQuyDinh().trim();
        if (!value.matches("\\d+(\\.\\d+)?%")) {
            throw new IllegalStateException("Tỷ lệ phạt QD002 không hợp lệ: " + value);
        }
        return new BigDecimal(value.substring(0, value.length() - 1))
            .divide(BigDecimal.valueOf(100));
    }

    private QuyDinh policy(String code, LocalDate date) {
        QuyDinh policy = quyDinhRepository.findById(code)
            .orElseThrow(() -> new IllegalStateException("Thiếu quy định " + code));
        if (policy.getNgayApDung().isAfter(date)) {
            throw new IllegalStateException("Quy định " + code + " chưa có hiệu lực");
        }
        return policy;
    }

    private String newInvoiceId() {
        StringBuilder id = new StringBuilder("H");
        for (int i = 0; i < 9; i++) {
            id.append(ID_CHARACTERS.charAt(RANDOM.nextInt(ID_CHARACTERS.length())));
        }
        return id.toString();
    }
}
