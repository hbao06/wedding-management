# Git Flow — Hệ thống Quản lý Tiệc cưới

> Sơ đồ nhánh, phân công và quy tắc làm việc của nhóm (Báo cáo Lab 7, Mục 3).

## 1. Sơ đồ Git Flow

```text
main                      ●──────────●
                          │          │
develop         ●─────────┼──────────┼─────────●
                │         │          │         │
feature/login   └─●──●────┘          │         │
                                     │         │
feature/booking     └─●──●──●────────┘         │
                                                   │
feature/payment         └─●──●──●──●─────────────┘

feature/project-setup ──●──●──●──●
                        (khung mã nguồn, merge đầu tiên)
```

main: phiên bản ổn định, sẵn sàng phát hành (gắn tag, VD v0.1.0). Chỉ merge từ develop qua Pull Request.
develop: nhánh tích hợp, nhánh mặc định của repo. Chỉ merge từ feature/_ qua Pull Request.
feature/_: nhánh tính năng, tách từ develop, merge ngược vào develop qua Pull Request.
release/_, hotfix/_: tạo khi phát sinh nhu cầu chuẩn bị phát hành / sửa lỗi khẩn cấp.

## 2. Vai trò các nhánh

| Nhánh | Mục đích | Người phụ trách |
| --- | --- | --- |
| `main` | Phiên bản ổn định, sẵn sàng phát hành (tag `v0.1.0`) | Chỉ merge từ `develop` qua PR |
| `develop` | Tích hợp các chức năng đang phát triển | Chỉ merge từ `feature/*` qua PR |
| `feature/project-setup` | Khung mã nguồn: CSDL, Backend, Frontend | Lê Trung Bảo |
| `feature/login` | Đăng nhập, phân quyền (`AccountService`) | Trương Huỳnh Hoài Bảo |
| `feature/booking` | Lập phiếu đặt tiệc cưới (`BookingService`) | Chung Nguyễn Minh Trí |
| `feature/payment` | Hóa đơn, thanh toán, phạt trễ hạn (`PaymentService`) | Lê Trung Bảo |
| `release/*`, `hotfix/*` | Chuẩn bị phát hành / sửa lỗi khẩn cấp | Tạo khi phát sinh nhu cầu |

## 3. Phân công nhánh và review

Nhóm có 3 thành viên nên gộp nhiệm vụ: người tạo repo phụ trách đăng nhập; người thiết kế CSDL ở Lab 6 dựng khung mã nguồn và chức năng thanh toán. Mỗi PR do một thành viên khác review theo vòng tròn để ai cũng vừa có PR của mình vừa có nhận xét review.

| Pull Request | Người tạo | Người review |
| --- | --- | --- |
| `feature/project-setup` → `develop` | Lê Trung Bảo | Chung Nguyễn Minh Trí |
| `feature/login` → `develop` | Trương Huỳnh Hoài Bảo | Lê Trung Bảo |
| `feature/booking` → `develop` | Chung Nguyễn Minh Trí | Trương Huỳnh Hoài Bảo |
| `feature/payment` → `develop` | Lê Trung Bảo | Chung Nguyễn Minh Trí |

## 4. Quy tắc làm việc

1. **Không commit trực tiếp lên `main` và `develop`.**
2. Trước khi làm việc: `git switch develop` → `git pull origin develop` → tạo nhánh mới từ `develop`.
3. Mỗi nhánh chỉ sửa các tệp thuộc chức năng của mình; các tệp dùng chung (định tuyến `App.jsx`, DTO, repository) đã được tạo sẵn trong khung mã nguồn.
4. Thông điệp commit ngắn gọn, dạng mệnh lệnh: `Add ...`, `Fix ...`, `Update ...`
5. Trước khi tạo PR: `mvn test` và `npm run build` phải chạy thành công.
6. Không commit `.env`, mật khẩu, dữ liệu khách hàng thật, `node_modules/`, `target/`, `dist/`.
