package com.tieccuoi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tieccuoi.dto.PaymentPreviewResponse;
import com.tieccuoi.model.HoaDonThanhToan;
import com.tieccuoi.model.PhieuDatTiecCuoi;
import com.tieccuoi.model.QuyDinh;
import com.tieccuoi.repository.HoaDonThanhToanRepository;
import com.tieccuoi.repository.PhieuDatTiecCuoiRepository;
import com.tieccuoi.repository.QuyDinhRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;

class PaymentServiceTest {
    private final PhieuDatTiecCuoiRepository phieuRepository = mock(PhieuDatTiecCuoiRepository.class);
    private final HoaDonThanhToanRepository hoaDonRepository = mock(HoaDonThanhToanRepository.class);
    private final QuyDinhRepository quyDinhRepository = mock(QuyDinhRepository.class);
    private final PaymentService service = new PaymentService(phieuRepository, hoaDonRepository, quyDinhRepository);
    private final LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    private PhieuDatTiecCuoi phieu;

    @BeforeEach
    void setUp() {
        phieu = mock(PhieuDatTiecCuoi.class);
        when(phieu.getMaPhieu()).thenReturn("P001");
        when(phieu.getNgayDaiTiec()).thenReturn(today.minusDays(2));
        when(phieu.getTongTienDuKien()).thenReturn(new BigDecimal("10000000"));
        when(phieu.getTienDatCoc()).thenReturn(new BigDecimal("3000000"));
        when(phieu.getTrangThai()).thenReturn("Đã đặt cọc");
        when(phieuRepository.findById("P001")).thenReturn(Optional.of(phieu));
        when(phieuRepository.findForPayment("P001")).thenReturn(Optional.of(phieu));

        QuyDinh enabled = mock(QuyDinh.class);
        when(enabled.getNgayApDung()).thenReturn(today.minusYears(1));
        when(enabled.getGiaTriQuyDinh()).thenReturn("true");
        QuyDinh rate = mock(QuyDinh.class);
        when(rate.getNgayApDung()).thenReturn(today.minusYears(1));
        when(rate.getGiaTriQuyDinh()).thenReturn("1%");
        when(quyDinhRepository.findById("QD003")).thenReturn(Optional.of(enabled));
        when(quyDinhRepository.findById("QD002")).thenReturn(Optional.of(rate));
    }

    @Test
    void penaltyUsesFullInvoiceAmountBeforeDeposit() {
        PaymentPreviewResponse preview = service.preview("P001", today);

        assertEquals(new BigDecimal("200000"), preview.tienPhatTreHan());
        assertEquals(new BigDecimal("7200000"), preview.soTienCanThu());
        assertEquals(2, preview.soNgayTre());
    }

    @Test
    void paymentOnWeddingDayHasNoPenalty() {
        PaymentPreviewResponse preview = service.preview("P001", today.minusDays(2));

        assertEquals(BigDecimal.ZERO, preview.tienPhatTreHan());
        assertEquals(new BigDecimal("7000000"), preview.soTienCanThu());
    }

    @Test
    void confirmationSavesPaidInvoiceAndCompletesBooking() {
        PaymentPreviewResponse result = service.confirm("P001", today,
                new BigDecimal("7200000"), "NV001");

        ArgumentCaptor<HoaDonThanhToan> invoice = ArgumentCaptor.forClass(HoaDonThanhToan.class);
        verify(hoaDonRepository).saveAndFlush(invoice.capture());
        assertEquals(new BigDecimal("200000"), invoice.getValue().getTienPhatTreHan());
        assertEquals(BigDecimal.ZERO, invoice.getValue().getSoTienConLai());
        assertEquals(today, invoice.getValue().getNgayThanhToan());
        assertEquals(true, result.daThanhToan());
        verify(phieu).setTrangThai("Hoàn tất");
    }

    @Test
    void confirmationRejectsChangedAmount() {
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.confirm("P001", today, new BigDecimal("7000000"), "NV001"))
            .getStatusCode().value());
        verify(hoaDonRepository, never()).saveAndFlush(any());
    }

    @Test
    void confirmationRejectsAlreadyPaidBooking() {
        when(hoaDonRepository.existsByMaPhieu("P001")).thenReturn(true);

        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.confirm("P001", today, new BigDecimal("7200000"), "NV001"))
            .getStatusCode().value());
        verify(hoaDonRepository, never()).saveAndFlush(any());
    }
}
