# Hệ thống Quản lý Tiệc Cưới

Đồ án môn Công nghệ Phần mềm xây dựng hệ thống hỗ trợ quản lý đặt tiệc cưới, sảnh tiệc, dịch vụ và thanh toán. Repo này cũng là sản phẩm thực hành Lab 7 về Git/GitHub, Git Flow, Pull Request và code review.

> **Tình trạng repo:** hiện tại repo đang có báo cáo Lab 7; các thư mục mã nguồn `backend/`, `frontend/` và `database/` chưa có trong checkout này. Các hướng dẫn chạy bên dưới áp dụng sau khi những phần mã nguồn tương ứng được bổ sung.

## Thành viên

| Thành viên | Vai trò / nhánh phụ trách |
|---|---|
| Trương Huỳnh Hoài Bảo | Nhóm trưởng, repository, `feature/login` |
| Chung Nguyễn Minh Trí | `feature/booking` |
| Lê Trung Bảo | `feature/project-setup`, `feature/payment` |

## Công nghệ dự kiến

- **Backend:** Java 17, Spring Boot 3, Maven
- **Frontend:** React, Vite, Node.js 20.19+ hoặc 22.12+
- **Cơ sở dữ liệu:** MySQL 8
- **Kiểm thử:** JUnit 5

## Cấu trúc dự án dự kiến

```text
wedding-management/
├── backend/          # REST API Spring Boot
├── frontend/         # Giao diện React
├── database/         # schema.sql và dữ liệu mẫu
├── docs/             # Tài liệu kiến trúc, CSDL, giao diện, Git Flow
├── .github/          # Mẫu Pull Request
└── README.md
```

## Yêu cầu cài đặt

Cài đặt các công cụ sau trước khi chạy dự án:

- JDK 17 trở lên
- Maven 3.9 trở lên (hoặc Maven tích hợp trong IntelliJ IDEA)
- MySQL 8.0.16 trở lên
- Node.js 20.19+ hoặc 22.12+
- Git

## Cài đặt và chạy

### 1. Lấy mã nguồn

```bash
git clone <URL_REPOSITORY>
cd wedding-management
```

Thay `<URL_REPOSITORY>` bằng đường dẫn repository GitHub của nhóm.

### 2. Tạo cơ sở dữ liệu

Sau khi có mã nguồn, tạo database `wedding_db` và chạy các tập lệnh SQL theo thứ tự:

```bash
mysql -u root -p -e "CREATE DATABASE wedding_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p wedding_db < database/schema.sql
mysql -u root -p wedding_db < database/seed.sql
```

Nếu tên database hoặc vị trí tệp SQL khác, dùng giá trị trong cấu hình dự án.

### 3. Cấu hình Backend

Tạo tệp `backend/.env` trên máy cá nhân (không commit tệp này) và thiết lập thông tin kết nối MySQL. Ví dụ:

```dotenv
DB_URL=jdbc:mysql://localhost:3306/wedding_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh
DB_USERNAME=root
DB_PASSWORD=<MAT_KHAU_MYSQL>
```

Khởi động Backend:

```bash
cd backend
mvn spring-boot:run
```

Mặc định API dự kiến chạy tại `http://localhost:8080`. Có thể kiểm tra tình trạng ứng dụng tại `http://localhost:8080/api/health`.

### 4. Cấu hình và chạy Frontend

Mở terminal khác:

```bash
cd frontend
npm install
npm run dev
```

Mở địa chỉ Vite hiển thị trong terminal (thường là `http://localhost:5173`). Nếu Backend chạy ở địa chỉ khác, cập nhật cấu hình API trong Frontend.

## API dự kiến

Các endpoint dưới đây được liệt kê trong báo cáo Lab 7; cần đối chiếu với mã nguồn sau khi Backend được bổ sung.

| Phương thức | Endpoint | Chức năng |
|---|---|---|
| `GET` | `/api/health` | Kiểm tra Backend và kết nối cơ sở dữ liệu |
| `POST` | `/api/auth/login` | Đăng nhập |
| `GET` | `/api/sanh`, `/api/sanh/con-trong` | Danh sách sảnh và sảnh còn trống |
| `GET` | `/api/dich-vu` | Danh sách dịch vụ |
| `POST`, `GET` | `/api/phieu`, `/api/phieu/{maPhieu}` | Tạo và xem phiếu đặt tiệc |
| `GET` | `/api/thanh-toan/phieu?q=` | Tra cứu phiếu cần thanh toán |
| `GET` | `/api/thanh-toan/phieu/{maPhieu}/xem-truoc` | Xem trước thanh toán và tiền phạt |
| `POST` | `/api/thanh-toan/hoa-don` | Xác nhận thanh toán và lập hóa đơn |

## Kiểm thử và build

Chạy các lệnh sau từ thư mục tương ứng sau khi mã nguồn đã có:

```bash
# Backend
cd backend
mvn test

# Frontend
cd frontend
npm run build
```

## Quy trình Git/GitHub

Nhóm sử dụng `main` cho phiên bản ổn định, `develop` để tích hợp và các nhánh `feature/*` cho chức năng. Tạo nhánh mới từ `develop` và gửi Pull Request về `develop` để thành viên khác review.

```bash
git switch develop
git pull origin develop
git switch -c feature/ten-chuc-nang
# Thực hiện thay đổi và commit
git status
git add <tep-can-commit>
git commit -m "Add mo-ta-thay-doi"
git push -u origin feature/ten-chuc-nang
```

Trước khi tạo Pull Request, chạy kiểm thử Backend và build Frontend. Không commit trực tiếp lên `main` hoặc `develop`; không commit `.env`, mật khẩu, dữ liệu khách hàng thật, `node_modules/`, `target/` hoặc `dist/`.

## Tài liệu

- [Báo cáo thực hành Lab 7](BaoCao_Lab7_QuanLyTiecCuoi.docx)
- Tài liệu kiến trúc, thiết kế cơ sở dữ liệu, giao diện và Git Flow sẽ được đặt trong `docs/` khi thư mục này được bổ sung.
