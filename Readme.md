# 💍 Hệ thống Quản lý Tiệc cưới

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)

Phần mềm hỗ trợ trung tâm tổ chức tiệc cưới quản lý **sảnh tiệc, phiếu đặt tiệc, tiền đặt cọc, hóa đơn thanh toán và doanh thu** — thay cho sổ sách và các tệp Excel rời rạc.

Đồ án môn **Công nghệ phần mềm** — Lớp N2.1.

---

## Mục lục

- [Bài toán](#bài-toán)
- [Người dùng hệ thống](#người-dùng-hệ-thống)
- [Chức năng](#chức-năng)
- [Quy định nghiệp vụ](#quy-định-nghiệp-vụ)
- [Kiến trúc](#kiến-trúc)
- [Công nghệ](#công-nghệ)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Cài đặt và chạy](#cài-đặt-và-chạy)
- [API](#api)
- [Cơ sở dữ liệu](#cơ-sở-dữ-liệu)
- [Kiểm thử](#kiểm-thử)
- [Quy trình làm việc nhóm](#quy-trình-làm-việc-nhóm)
- [Tiến độ dự án](#tiến-độ-dự-án)
- [Tài liệu thiết kế](#tài-liệu-thiết-kế)
- [Thành viên](#thành-viên)

---

## Bài toán

Mỗi khi có khách đặt tiệc, nhà hàng phải kiểm tra sảnh còn trống theo ngày và ca, tư vấn lập phiếu đặt tiệc, nhận tiền cọc giữ chỗ, rồi lập hóa đơn sau khi tiệc kết thúc. Làm thủ công dễ dẫn đến:

- Nhận **đặt trùng sảnh** trong cùng ngày, cùng ca.
- **Tính sai** tổng tiền dự kiến, tiền còn lại hoặc tiền phạt trễ hạn.
- Khó theo dõi khách **đã đặt cọc hay chưa**.
- Mất thời gian **tra cứu** tiệc cưới và **thống kê doanh thu** theo tháng, theo sảnh.

Hệ thống tự động hóa các khâu kiểm tra và tính toán này, đồng thời phân quyền rõ ràng cho từng bộ phận.

## Người dùng hệ thống

| Vai trò                          | Công việc chính trên hệ thống                                           |
| -------------------------------- | ----------------------------------------------------------------------- |
| **Khách hàng** (chú rể / cô dâu) | Xem sảnh còn trống, tra cứu phiếu đặt tiệc của mình                     |
| **Nhân viên Sales**              | Kiểm tra sảnh trống, lập / cập nhật phiếu đặt tiệc, ghi nhận tiền cọc   |
| **Kế toán**                      | Lập hóa đơn thanh toán, tính tiền phạt trễ hạn, lập báo cáo doanh thu   |
| **Quản lý nhà hàng**             | Quản lý sảnh, dịch vụ, thay đổi quy định giá và mức phạt, xem doanh thu |
| **Quản trị hệ thống**            | Quản lý tài khoản, phân quyền người dùng                                |

## Chức năng

| Nhóm       | Chức năng                                                                           | Mã yêu cầu |
| ---------- | ----------------------------------------------------------------------------------- | ---------- |
| Tài khoản  | Đăng nhập, quản lý tài khoản, phân quyền theo vai trò                               | FR01–FR03  |
| Sảnh       | Thêm / sửa / xem sảnh; kiểm tra sảnh còn trống theo ngày và ca                      | FR04–FR05  |
| Đặt tiệc   | Lập phiếu đặt tiệc, chọn dịch vụ / món ăn, tính tổng tiền dự kiến, ghi nhận đặt cọc | FR06–FR09  |
| Tra cứu    | Tra cứu tiệc cưới theo tên, ngày, sảnh; cập nhật / hủy phiếu                        | FR10–FR11  |
| Thanh toán | Lập hóa đơn, tự tính tiền phạt trễ hạn, tính số tiền còn lại                        | FR12–FR14  |
| Báo cáo    | Doanh số theo tháng, doanh thu theo từng sảnh                                       | FR15–FR16  |
| Quy định   | Cập nhật loại sảnh, đơn giá tối thiểu, ca, dịch vụ, quy định phạt                   | FR17–FR18  |
| Đặt cọc    | Xử lý hoàn cọc / mất cọc khi khách hủy tiệc                                         | FR19       |

Trạng thái hiện thực của từng chức năng xem ở mục [Tiến độ dự án](#tiến-độ-dự-án).

## Quy định nghiệp vụ

| Mã      | Nội dung                                                                                                                                                                                                          | Hệ thống xử lý                                                                            |
| ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| **QĐ1** | 5 loại sảnh A–E với đơn giá bàn tối thiểu 1.000.000 / 1.100.000 / 1.200.000 / 1.400.000 / 1.600.000 đ. Số bàn không vượt quá sức chứa sảnh. Mỗi sảnh chỉ nhận **một tiệc cho mỗi ngày + ca** (Sáng / Trưa / Tối). | Kiểm tra khi lập phiếu; ràng buộc `UNIQUE(MaSanh, NgayDaiTiec, Ca)` trong CSDL            |
| **QĐ2** | Đặt cọc tối thiểu **30%** tổng tiền dự kiến. Không đặt cọc thì phiếu chỉ được **giữ chỗ tạm**.                                                                                                                    | Từ chối cọc dưới tỉ lệ; phiếu không cọc ở trạng thái _Tạm giữ_                            |
| **QĐ4** | Thanh toán đúng ngày đãi tiệc: không phạt. Thanh toán trễ: phạt **1% mỗi ngày** trên số tiền còn lại.                                                                                                             | Tự tính khi Kế toán chọn ngày thanh toán                                                  |
| **QĐ6** | Quản lý nhà hàng được thay đổi loại sảnh, đơn giá tối thiểu, ca, dịch vụ và bật / tắt quy định phạt.                                                                                                              | Tỉ lệ cọc, tỉ lệ phạt, bật / tắt phạt lưu trong bảng `QUYDINH`, không ghi cứng trong code |

## Kiến trúc

Hệ thống theo **kiến trúc 3 lớp**: giao diện React gọi REST API; lớp nghiệp vụ (Spring Boot service) kiểm tra quy định và tính toán; lớp dữ liệu (Spring Data JPA) đọc / ghi MySQL.

![Kiến trúc 3 lớp](docs/images/architecture.png)

| Lớp       | Thành phần                                                                              | Thư mục                                         |
| --------- | --------------------------------------------------------------------------------------- | ----------------------------------------------- |
| Giao diện | Các màn hình theo vai trò                                                               | `frontend/src/pages`, `frontend/src/components` |
| API       | REST controller, DTO, kiểm tra dữ liệu đầu vào                                          | `backend/.../controller`, `backend/.../dto`     |
| Nghiệp vụ | `AccountService`, `BookingService`, `PaymentService`, `ReportService`, `QuyDinhService` | `backend/.../service`                           |
| Dữ liệu   | 9 entity JPA và repository                                                              | `backend/.../model`, `backend/.../repository`   |
| CSDL      | MySQL, 9 bảng                                                                           | `database/schema.sql`                           |

Chi tiết: [docs/architecture.md](docs/architecture.md).

## Công nghệ

| Thành phần       | Công nghệ                                                           |
| ---------------- | ------------------------------------------------------------------- |
| Backend          | Java 17, Spring Boot 3.5 (Web, Data JPA, Validation), BCrypt, Maven |
| Frontend         | React 19, Vite, React Router, Axios                                 |
| Cơ sở dữ liệu    | MySQL 8 (bảng mã `utf8mb4`)                                         |
| Kiểm thử         | JUnit 5                                                             |
| Quản lý mã nguồn | Git, GitHub (Git Flow, Pull Request, Code Review)                   |

## Cấu trúc thư mục

```text
wedding-management/
├── backend/                 # REST API Spring Boot (cổng 8080)
│   ├── pom.xml
│   ├── .env.example         # mẫu cấu hình CSDL — sao chép thành .env
│   └── src/main/java/com/tieccuoi/
│       ├── config/          # CORS, mã hóa mật khẩu
│       ├── controller/      # REST API
│       ├── service/         # nghiệp vụ và quy định QĐ1–QĐ6
│       ├── repository/      # truy cập CSDL
│       ├── model/           # entity ánh xạ 9 bảng
│       ├── dto/             # dữ liệu nhận / trả qua API
│       └── exception/       # xử lý lỗi tập trung
├── frontend/                # Giao diện React + Vite (cổng 5173)
│   └── src/
│       ├── pages/           # Đăng nhập, Tổng quan, Lập phiếu, Kết quả, Thanh toán, Quản lý sảnh
│       ├── components/      # Layout (menu theo vai trò), Card, Alert
│       ├── services/        # api.js (Axios), auth.js
│       └── utils/           # định dạng tiền, ngày, trạng thái
├── database/
│   ├── schema.sql           # tạo database wedding_db và 9 bảng
│   └── seed.sql             # dữ liệu mẫu: sảnh, dịch vụ, quy định, tài khoản
├── docs/                    # tài liệu thiết kế, sơ đồ, Git Flow
├── .github/                 # mẫu mô tả Pull Request
└── README.md
```

## Cài đặt và chạy

### Yêu cầu

| Phần mềm | Phiên bản                                          |
| -------- | -------------------------------------------------- |
| JDK      | 17 trở lên                                         |
| Maven    | 3.9+ (hoặc dùng Maven có sẵn trong IntelliJ IDEA)  |
| MySQL    | 8.0.16+ (cần hỗ trợ `CHECK`) — khuyên dùng 8.4 LTS |
| Node.js  | 20.19+ hoặc 22.12+                                 |
| Git      | bản mới                                            |

### 1. Lấy mã nguồn

```bash
git clone https://github.com/hbao06/wedding-management.git
cd wedding-management
git switch develop
```

### 2. Tạo cơ sở dữ liệu

**Cách 1 — MySQL Workbench:** _File → Open SQL Script_ → mở `database/schema.sql` → bấm ⚡ để chạy; làm tương tự với `database/seed.sql`.

**Cách 2 — dòng lệnh** (chạy tại thư mục gốc repo):

```bash
mysql -u root -p --default-character-set=utf8mb4 -e "source database/schema.sql"
mysql -u root -p --default-character-set=utf8mb4 wedding_db -e "source database/seed.sql"
```

> ⚠️ `schema.sql` **xóa và tạo lại** database `wedding_db`. Chỉ dùng trên máy phát triển.

### 3. Chạy Backend

```bash
cd backend
cp .env.example .env          # Windows CMD: copy .env.example .env
```

Mở `backend/.env`, sửa `DB_PASSWORD` thành mật khẩu MySQL trên máy bạn, rồi chạy:

```bash
mvn spring-boot:run
```

Hoặc mở thư mục `backend` bằng IntelliJ IDEA và chạy lớp `Application`.

Kiểm tra: mở <http://localhost:8080/api/health> — kết quả đúng có `"database": "UP"` và `"soSanh": 5`.

### 4. Chạy Frontend

```bash
cd frontend
npm install
npm run dev
```

Mở <http://localhost:5173>. Trang **Tổng quan** báo Backend _Đang chạy_ và CSDL _Đã kết nối_ là hệ thống đã thông suốt.

> Backend chạy ở cổng khác 8080? Tạo `frontend/.env` với `VITE_API_URL=http://localhost:<cổng>/api`.

### Tài khoản mẫu

Chỉ dùng trên môi trường phát triển (tạo bởi `seed.sql`):

| Tài khoản | Mật khẩu | Vai trò           |
| --------- | -------- | ----------------- |
| `sales1`  | `123456` | Nhân viên Sales   |
| `ketoan1` | `123456` | Kế toán           |
| `quanly1` | `123456` | Quản lý nhà hàng  |
| `admin`   | `123456` | Quản trị hệ thống |

### Lỗi thường gặp

| Thông báo                                     | Cách xử lý                                |
| --------------------------------------------- | ----------------------------------------- |
| `Could not resolve placeholder 'DB_PASSWORD'` | Chưa tạo `backend/.env` từ `.env.example` |
| `Access denied for user 'root'`               | Sai mật khẩu MySQL trong `.env`           |
| `Communications link failure`                 | Dịch vụ MySQL chưa chạy                   |
| `Schema-validation: missing table`            | Chưa chạy `database/schema.sql`           |
| Frontend báo không kết nối được Backend       | Backend chưa chạy hoặc khác cổng 8080     |

## API

Tất cả API có tiền tố `/api`, nhận và trả JSON. Lỗi trả về dạng `{ "message": "...", "details": [...] }`.

| Phương thức | Đường dẫn                                                  | Chức năng                             |
| ----------- | ---------------------------------------------------------- | ------------------------------------- |
| `GET`       | `/api/health`                                              | Kiểm tra Backend và kết nối CSDL      |
| `POST`      | `/api/auth/login`                                          | Đăng nhập                             |
| `GET`       | `/api/sanh`                                                | Danh sách sảnh                        |
| `GET`       | `/api/sanh/con-trong?ngay=2026-12-20&ca=TOI`               | Sảnh còn trống theo ngày, ca          |
| `GET`       | `/api/dich-vu`                                             | Danh mục dịch vụ / món ăn             |
| `POST`      | `/api/phieu`                                               | Lập phiếu đặt tiệc cưới               |
| `GET`       | `/api/phieu/{maPhieu}`                                     | Xem chi tiết phiếu                    |
| `GET`       | `/api/thanh-toan/phieu?q=`                                 | Tra cứu phiếu cần thanh toán          |
| `GET`       | `/api/thanh-toan/phieu/{maPhieu}/xem-truoc?ngayThanhToan=` | Tính trước tiền phạt, số tiền còn lại |
| `POST`      | `/api/thanh-toan/hoa-don`                                  | Xác nhận thanh toán, lập hóa đơn      |

API của chức năng chưa hiện thực trả về `501 Not Implemented`.

## Cơ sở dữ liệu

9 bảng, ánh xạ 1–1 với các lớp trong Class Diagram:

| Bảng                      | Nội dung                                                     |
| ------------------------- | ------------------------------------------------------------ |
| `NGUOIDUNG`               | Tài khoản chung: tên đăng nhập, mật khẩu băm BCrypt, vai trò |
| `KHACHHANG`, `NHANVIEN`   | Kế thừa `NGUOIDUNG` (dùng chung khóa chính)                  |
| `SANH`                    | Sảnh, loại A–E, số bàn tối đa, đơn giá tối thiểu             |
| `PHIEUDATTIECCUOI`        | Phiếu đặt tiệc — bảng trung tâm                              |
| `DICHVU`, `CHITIETDICHVU` | Danh mục dịch vụ và dịch vụ chọn trong từng phiếu            |
| `HOADONTHANHTOAN`         | Hóa đơn — mỗi phiếu tối đa một hóa đơn                       |
| `QUYDINH`                 | Tham số nghiệp vụ có thể thay đổi (QĐ6)                      |

![ERD](docs/images/erd.png)

Chi tiết và các điều chỉnh khi hiện thực: [docs/database-design.md](docs/database-design.md).

## Kiểm thử

```bash
cd backend && mvn test         # kiểm thử đơn vị, không cần CSDL
cd frontend && npm run build   # kiểm tra Frontend biên dịch được
```

| Lớp kiểm thử           | Nội dung                                                       |
| ---------------------- | -------------------------------------------------------------- |
| `IdGeneratorTest`      | Sinh mã 10 ký tự, không trùng                                  |
| `PhieuDatTiecCuoiTest` | Tổng tiền dự kiến, trạng thái đặt cọc, cọc tối thiểu 30% (QĐ2) |
| `PaymentServiceTest`   | Phạt trễ hạn 1%/ngày, không phạt khi đúng hạn, làm tròn (QĐ4)  |

## Quy trình làm việc nhóm

Nhóm làm theo **Git Flow**:

| Nhánh                     | Mục đích                                     |
| ------------------------- | -------------------------------------------- |
| `main`                    | Phiên bản ổn định, gắn tag phát hành         |
| `develop`                 | Tích hợp các chức năng — nhánh mặc định      |
| `feature/<ten-chuc-nang>` | Phát triển từng chức năng, tách từ `develop` |

```bash
git switch develop && git pull origin develop
git switch -c feature/ten-chuc-nang
# ... sửa code ...
git add <tệp>
git commit -m "Add <mô tả ngắn>"
git push -u origin feature/ten-chuc-nang
```

Sau đó tạo **Pull Request vào `develop`**, nhờ một thành viên khác review và Approve rồi mới merge.

**Quy ước**

- Không commit trực tiếp lên `main` hoặc `develop`.
- Thông điệp commit tiếng Anh, dạng mệnh lệnh: `Add ...`, `Fix ...`, `Update ...`.
- Trước khi tạo PR: `mvn test` và `npm run build` phải chạy thành công.
- **Không commit** `.env`, mật khẩu, dữ liệu khách hàng thật, `node_modules/`, `target/`, `dist/`.

Sơ đồ nhánh và phân công review: [docs/git-flow.md](docs/git-flow.md).

## Tiến độ dự án

**Phân tích và thiết kế**

- [x] Xác định bài toán, phạm vi, yêu cầu nghiệp vụ
- [x] Đặc tả yêu cầu (SRS), Use Case, Product Backlog
- [x] Mô hình hóa UML: Use Case, Activity, Class, Sequence Diagram
- [x] Thiết kế kiến trúc 3 lớp, cơ sở dữ liệu, giao diện

**Hiện thực**

- [ ] Khung mã nguồn: CSDL, Backend, Frontend — `feature/project-setup`
- [ ] Đăng nhập, phân quyền — `feature/login`
- [ ] Kiểm tra sảnh trống, lập phiếu đặt tiệc, đặt cọc — `feature/booking`
- [ ] Lập hóa đơn, phạt trễ hạn, thanh toán — `feature/payment`
- [ ] Quản lý sảnh và dịch vụ
- [ ] Tra cứu, cập nhật, hủy phiếu đặt tiệc; hoàn cọc
- [ ] Báo cáo doanh thu theo tháng, theo sảnh
- [ ] Thay đổi quy định (QĐ6) trên giao diện
- [ ] Xác thực bằng token (JWT), khóa tài khoản khi nhập sai nhiều lần

## Tài liệu thiết kế

| Tài liệu                                           | Nội dung                                                                   |
| -------------------------------------------------- | -------------------------------------------------------------------------- |
| [docs/architecture.md](docs/architecture.md)       | Kiến trúc 3 lớp, luồng xử lý lập phiếu và thanh toán, ánh xạ Class Diagram |
| [docs/database-design.md](docs/database-design.md) | ERD, danh sách bảng, ràng buộc, ghi chú hiện thực                          |
| [docs/ui-design.md](docs/ui-design.md)             | Thiết kế 5 màn hình chính và thông báo phản hồi                            |
| [docs/git-flow.md](docs/git-flow.md)               | Sơ đồ nhánh, phân công, quy tắc làm việc                                   |

<p align="center">
  <img src="docs/images/ui-result.png" alt="Màn hình kết quả lập phiếu đặt tiệc" width="49%">
  <img src="docs/images/ui-payment.png" alt="Màn hình tra cứu hóa đơn và thanh toán" width="49%">
</p>
<p align="center"><em>Thiết kế giao diện: kết quả lập phiếu đặt tiệc (trái) và thanh toán (phải) — xem đủ 5 màn hình ở <a href="docs/ui-design.md">docs/ui-design.md</a></em></p>

## Thành viên

| Họ và tên             | MSSV     | Vai trò                     |
| --------------------- | -------- | --------------------------- |
| Trương Huỳnh Hoài Bảo | 52400170 | Nhóm trưởng, Product Owner  |
| Chung Nguyễn Minh Trí | 52400248 | Scrum Master, Developer     |
| Lê Trung Bảo          | 52400042 | Developer, Tester, Database |
