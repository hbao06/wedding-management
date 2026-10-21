package com.tieccuoi.controller;

import com.tieccuoi.dto.BookingRequest;
import com.tieccuoi.dto.BookingResponse;
import com.tieccuoi.dto.SanhResponse;
import com.tieccuoi.service.BookingService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/halls")
    public List<SanhResponse> layDanhSachSanh() {
        return bookingService.layDanhSachSanh();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse taoPhieu(@RequestBody BookingRequest request) {
        return bookingService.taoPhieu(request);
    }

    @GetMapping
    public List<BookingResponse> layDanhSachPhieu() {
        return bookingService.layDanhSachPhieu();
    }

    @GetMapping("/{maPhieu}")
    public BookingResponse layTheoMaPhieu(@PathVariable String maPhieu) {
        return bookingService.layTheoMaPhieu(maPhieu);
    }

    // Trả thông báo lỗi dạng {"message": "..."} để giao diện hiển thị ngay tại form
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> xuLyLoi(ResponseStatusException ex) {
        String thongBao = ex.getReason() == null ? "Đã xảy ra lỗi" : ex.getReason();
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", thongBao));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> xuLyDuLieuSai(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
            .body(Map.of("message", "Dữ liệu gửi lên không đúng định dạng"));
    }
}
