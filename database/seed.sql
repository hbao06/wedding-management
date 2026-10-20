USE wedding_db;

INSERT INTO SANH (MaSanh, TenSanh, LoaiSanh, SoBanToiDa, DonGiaToiThieu) VALUES
    ('S001', 'Sảnh A', 'A', 20, 1000000),
    ('S002', 'Sảnh B', 'B', 25, 1100000),
    ('S003', 'Sảnh C', 'C', 30, 1200000),
    ('S004', 'Sảnh D', 'D', 35, 1400000),
    ('S005', 'Sảnh E', 'E', 40, 1600000)
ON DUPLICATE KEY UPDATE MaSanh = MaSanh;

INSERT INTO DICHVU (MaDV, TenDichVu, DonGia, DonViTinh) VALUES
    ('DV001', 'Trang trí sân khấu', 3000000, 'tiệc'),
    ('DV002', 'Ban nhạc', 5000000, 'tiệc'),
    ('DV003', 'Chụp ảnh', 4000000, 'tiệc')
ON DUPLICATE KEY UPDATE MaDV = MaDV;

INSERT INTO QUYDINH (MaQD, TenQuyDinh, GiaTriQuyDinh, NgayApDung) VALUES
    ('QD001', 'Tỉ lệ đặt cọc tối thiểu', '30%', '2026-01-01'),
    ('QD002', 'Tỉ lệ phạt thanh toán trễ mỗi ngày', '1%', '2026-01-01'),
    ('QD003', 'Áp dụng phạt thanh toán trễ', 'true', '2026-01-01')
ON DUPLICATE KEY UPDATE MaQD = MaQD;
