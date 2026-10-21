package com.tieccuoi.service;

import com.tieccuoi.dto.BookingRequest;
import com.tieccuoi.dto.BookingResponse;
import com.tieccuoi.dto.SanhResponse;
import com.tieccuoi.model.PhieuDatTiecCuoi;
import com.tieccuoi.model.QuyDinh;
import com.tieccuoi.model.Sanh;
import com.tieccuoi.repository.KhachHangRepository;
import com.tieccuoi.repository.NhanVienRepository;
import com.tieccuoi.repository.PhieuDatTiecCuoiRepository;
import com.tieccuoi.repository.QuyDinhRepository;
import com.tieccuoi.repository.SanhRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {

    private static final String MA_QD_TI_LE_COC = "QD001";
    private static final String SANH_HOAT_DONG = "Hoạt động";
    private static final String TRANG_THAI_DA_COC = "Đã đặt cọc";
    private static final Set<String> CA_HOP_LE = Set.of("SANG", "TRUA", "TOI");

    private final PhieuDatTiecCuoiRepository phieuRepository;
    private final SanhRepository sanhRepository;
    private final QuyDinhRepository quyDinhRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;

    public BookingService(
            PhieuDatTiecCuoiRepository phieuRepository,
            SanhRepository sanhRepository,
            QuyDinhRepository quyDinhRepository,
            KhachHangRepository khachHangRepository,
            NhanVienRepository nhanVienRepository) {
        this.phieuRepository = phieuRepository;
        this.sanhRepository = sanhRepository;
        this.quyDinhRepository = quyDinhRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    public List<SanhResponse> layDanhSachSanh() {
        return sanhRepository.findAll(Sort.by("maSanh")).stream()
            .filter(sanh -> SANH_HOAT_DONG.equals(sanh.getTrangThai()))
            .map(SanhResponse::from)
            .toList();
    }

    public BookingResponse taoPhieu(BookingRequest req) {
        kiemTraDuLieuDauVao(req);

        String maKH = req.maKH().trim();
        String maNV = req.maNV().trim();
        String maSanh = req.maSanh().trim();
        String ca = req.ca().trim().toUpperCase(Locale.ROOT);
        BigDecimal donGiaBan = req.donGiaBan().setScale(0, RoundingMode.HALF_UP);
        BigDecimal tienDatCoc = req.tienDatCoc().setScale(0, RoundingMode.HALF_UP);

        if (!khachHangRepository.existsById(maKH)) {
            throw loiDuLieu("Khách hàng không tồn tại: " + maKH);
        }
        if (!nhanVienRepository.existsById(maNV)) {
            throw loiDuLieu("Nhân viên lập phiếu không tồn tại: " + maNV);
        }
        Sanh sanh = sanhRepository.findById(maSanh)
            .orElseThrow(() -> loiDuLieu("Sảnh không tồn tại: " + maSanh));

        // QĐ1: số bàn không vượt sức chứa, đơn giá không thấp hơn mức tối thiểu của loại sảnh
        if (!SANH_HOAT_DONG.equals(sanh.getTrangThai())) {
            throw loiDuLieu("Sảnh " + sanh.getTenSanh() + " hiện không hoạt động");
        }
        if (req.soLuongBan() > sanh.getSoBanToiDa()) {
            throw loiDuLieu("Số bàn vượt quá sức chứa của " + sanh.getTenSanh()
                + " (tối đa " + sanh.getSoBanToiDa() + " bàn)");
        }
        if (donGiaBan.compareTo(sanh.getDonGiaToiThieu()) < 0) {
            throw loiDuLieu("Đơn giá thấp hơn mức tối thiểu của " + sanh.getTenSanh()
                + " (" + dinhDangTien(sanh.getDonGiaToiThieu()) + "/bàn)");
        }

        if (phieuRepository.existsByMaSanhAndNgayDaiTiecAndCa(maSanh, req.ngayDaiTiec(), ca)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Sảnh đã có người đặt trong ca này");
        }

        // Tổng tiền dự kiến (hiện chưa gồm dịch vụ phát sinh)
        BigDecimal tongTienDuKien = donGiaBan.multiply(BigDecimal.valueOf(req.soLuongBan()));

        // QĐ2: tiền cọc tối thiểu theo tỉ lệ cấu hình ở bảng QUYDINH
        BigDecimal phanTramCoc = layPhanTramCocToiThieu();
        BigDecimal cocToiThieu = tongTienDuKien
            .multiply(phanTramCoc)
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.CEILING);
        if (tienDatCoc.compareTo(cocToiThieu) < 0) {
            throw loiDuLieu("Tiền cọc chưa đạt tỉ lệ tối thiểu " + phanTramCoc.stripTrailingZeros().toPlainString()
                + "% (cần ít nhất " + dinhDangTien(cocToiThieu) + ")");
        }

        PhieuDatTiecCuoi phieu = new PhieuDatTiecCuoi(
            sinhMaPhieu(),
            maKH,
            maSanh,
            maNV,
            req.tenChuRe().trim(),
            req.tenCoDau().trim(),
            req.ngayDaiTiec(),
            ca,
            req.soLuongBan(),
            donGiaBan,
            tienDatCoc,
            tongTienDuKien,
            TRANG_THAI_DA_COC,
            LocalDateTime.now()
        );

        try {
            phieuRepository.saveAndFlush(phieu);
        } catch (DataIntegrityViolationException e) {
            // UNIQUE(MaSanh, NgayDaiTiec, Ca) ở CSDL chặn trường hợp hai yêu cầu cùng lúc
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Sảnh đã có người đặt trong ca này (hoặc mã phiếu bị trùng, vui lòng thử lại)");
        }

        return BookingResponse.from(phieu, sanh.getTenSanh());
    }

    public BookingResponse layTheoMaPhieu(String maPhieu) {
        PhieuDatTiecCuoi phieu = phieuRepository.findById(maPhieu)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Không tìm thấy phiếu đặt tiệc: " + maPhieu));
        String tenSanh = sanhRepository.findById(phieu.getMaSanh())
            .map(Sanh::getTenSanh)
            .orElse(phieu.getMaSanh());
        return BookingResponse.from(phieu, tenSanh);
    }

    public List<BookingResponse> layDanhSachPhieu() {
        Map<String, String> tenSanhTheoMa = sanhRepository.findAll().stream()
            .collect(Collectors.toMap(Sanh::getMaSanh, Sanh::getTenSanh));
        return phieuRepository.findAll(Sort.by(Sort.Direction.DESC, "ngayLapPhieu")).stream()
            .map(phieu -> BookingResponse.from(
                phieu, tenSanhTheoMa.getOrDefault(phieu.getMaSanh(), phieu.getMaSanh())))
            .toList();
    }

    private void kiemTraDuLieuDauVao(BookingRequest req) {
        if (req == null) {
            throw loiDuLieu("Thiếu dữ liệu phiếu đặt tiệc");
        }
        yeuCauChuoi(req.maKH(), "Mã khách hàng", 10);
        yeuCauChuoi(req.maNV(), "Mã nhân viên", 10);
        yeuCauChuoi(req.maSanh(), "Sảnh", 10);
        yeuCauChuoi(req.tenChuRe(), "Tên chú rể", 100);
        yeuCauChuoi(req.tenCoDau(), "Tên cô dâu", 100);

        if (req.ngayDaiTiec() == null) {
            throw loiDuLieu("Ngày đãi tiệc không được để trống");
        }
        if (!req.ngayDaiTiec().isAfter(LocalDate.now())) {
            throw loiDuLieu("Ngày đãi tiệc phải sau ngày hôm nay");
        }

        yeuCauChuoi(req.ca(), "Ca đãi tiệc", 10);
        if (!CA_HOP_LE.contains(req.ca().trim().toUpperCase(Locale.ROOT))) {
            throw loiDuLieu("Ca đãi tiệc không hợp lệ (SANG, TRUA hoặc TOI)");
        }

        if (req.soLuongBan() == null || req.soLuongBan() <= 0) {
            throw loiDuLieu("Số lượng bàn phải lớn hơn 0");
        }
        if (req.donGiaBan() == null || req.donGiaBan().signum() <= 0) {
            throw loiDuLieu("Đơn giá bàn phải lớn hơn 0");
        }
        if (req.tienDatCoc() == null || req.tienDatCoc().signum() < 0) {
            throw loiDuLieu("Tiền đặt cọc không được âm");
        }
    }

    private BigDecimal layPhanTramCocToiThieu() {
        QuyDinh quyDinh = quyDinhRepository.findById(MA_QD_TI_LE_COC)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Chưa cấu hình quy định tỉ lệ đặt cọc (" + MA_QD_TI_LE_COC + ")"));
        try {
            // Giá trị lưu dạng chuỗi, ví dụ "30%"
            BigDecimal phanTram = new BigDecimal(quyDinh.getGiaTriQuyDinh().replace("%", "").trim());
            if (phanTram.signum() < 0 || phanTram.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new NumberFormatException("Ngoài khoảng 0-100");
            }
            return phanTram;
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Quy định tỉ lệ đặt cọc không hợp lệ: " + quyDinh.getGiaTriQuyDinh());
        }
    }

    private String sinhMaPhieu() {
        int soTiepTheo = phieuRepository.findTopByOrderByMaPhieuDesc()
            .map(PhieuDatTiecCuoi::getMaPhieu)
            .filter(ma -> ma.matches("PD\\d{8}"))
            .map(ma -> Integer.parseInt(ma.substring(2)) + 1)
            .orElse(1);
        return String.format("PD%08d", soTiepTheo);
    }

    private static void yeuCauChuoi(String giaTri, String ten, int doDaiToiDa) {
        if (giaTri == null || giaTri.isBlank()) {
            throw loiDuLieu(ten + " không được để trống");
        }
        if (giaTri.trim().length() > doDaiToiDa) {
            throw loiDuLieu(ten + " quá dài (tối đa " + doDaiToiDa + " ký tự)");
        }
    }

    private static ResponseStatusException loiDuLieu(String thongBao) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, thongBao);
    }

    private static String dinhDangTien(BigDecimal soTien) {
        return String.format(Locale.US, "%,d", soTien.longValue()).replace(',', '.') + " đ";
    }
}
