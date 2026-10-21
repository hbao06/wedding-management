package com.tieccuoi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tieccuoi.model.NhanVien;
import com.tieccuoi.repository.NhanVienRepository;
import com.tieccuoi.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class PaymentControllerTest {
    private final PaymentService paymentService = mock(PaymentService.class);
    private final NhanVienRepository nhanVienRepository = mock(NhanVienRepository.class);
    private final PaymentController controller = new PaymentController(paymentService, nhanVienRepository);
    private final HttpSession session = mock(HttpSession.class);

    @Test
    void rejectsUnauthenticatedRequest() {
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> controller.search("", session)).getStatusCode().value());
        verifyNoInteractions(paymentService);
    }

    @Test
    void rejectsNonAccountant() {
        NhanVien employee = mock(NhanVien.class);
        when(session.getAttribute("maND")).thenReturn("NV001");
        when(nhanVienRepository.findById("NV001")).thenReturn(Optional.of(employee));
        when(employee.getTrangThai()).thenReturn(true);
        when(employee.getChucVu()).thenReturn("Sales");

        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> controller.search("", session)).getStatusCode().value());
        verifyNoInteractions(paymentService);
    }
}
