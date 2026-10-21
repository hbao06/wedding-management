# Hệ thống Quản lý Tiệc cưới

Đồ án môn Công nghệ phần mềm (Lab 7: Software Implementation & Version Control). Hệ thống hướng tới việc quản lý sảnh, phiếu đặt tiệc, tiền cọc, hóa đơn và thanh toán cho một trung tâm tiệc cưới. Thiết kế nghiệp vụ và kiến trúc kế thừa từ Lab 6.

**Trạng thái hiện tại:** nhánh `feature/project-setup` đã có CSDL 9 bảng, khung Backend Spring Boot, 9 entity/repository và giao diện React kiểm tra kết nối. API nghiệp vụ, đăng nhập và tài khoản mẫu chưa được triển khai.

## Thành viên và phân công

| Thành viên | MSSV | Vai trò | Nhánh phụ trách |
| --- | --- | --- | --- |
| Trương Huỳnh Hoài Bảo | 52400170 | Nhóm trưởng, Product Owner | `feature/project-setup`, `feature/payment` |
| Chung Nguyễn Minh Trí | 52400248 | Scrum Master, Developer | `feature/booking` |
| Lê Trung Bảo | 52400042 | Developer, Tester, Database | `feature/login` |

`main` giữ phiên bản ổn định; `develop` là nhánh tích hợp. Phân công review và quy tắc Git chi tiết nằm trong [docs/git-flow.md](docs/git-flow.md).

## Tài liệu thiết kế

| Tài liệu | Nội dung |
| --- | --- |
| [Kiến trúc](docs/architecture.md) | Kiến trúc 3 lớp và luồng xử lý |
| [Thiết kế CSDL](docs/database-design.md) | 9 bảng, quan hệ và ràng buộc |
| [Thiết kế giao diện](docs/ui-design.md) | Các màn hình và vai trò sử dụng |
| [Git Flow](docs/git-flow.md) | Nhánh, phân công, Pull Request và review |

Ảnh sơ đồ và giao diện mẫu nằm trong [`docs/image/`](docs/image/).

## Công nghệ và cấu trúc

- Backend: Java 17, Spring Boot 3.5.16, Maven, Spring Data JPA.
- Frontend: React 19, Vite 8, React Router 7, Axios.
- CSDL: MySQL 8, bảng mã `utf8mb4`.
- Kiểm thử dự kiến: JUnit 5; hiện chưa có test case tự động trong repo.

```text
wedding-management/
├── backend/
│   ├── pom.xml
│   ├── .env.example
│   └── src/main/
│       ├── java/com/tieccuoi/
│       │   ├── Application.java
│       │   ├── controller/       # GET /api/health
│       │   ├── service/          # kiểm tra kết nối CSDL
│       │   ├── model/            # 9 entity JPA
│       │   └── repository/       # 9 repository
│       └── resources/application.properties
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── App.jsx              # trang Tổng quan trạng thái
│       └── services/api.js       # Axios gọi Backend
├── database/
│   ├── schema.sql                # tạo wedding_db và 9 bảng
│   └── seed.sql                  # 5 sảnh, 3 dịch vụ, 3 quy định
└── docs/                         # tài liệu và ảnh thiết kế
```

## Yêu cầu cài đặt

| Phần mềm | Phiên bản |
| --- | --- |
| JDK | 17 trở lên |
| Maven | 3.9 trở lên; kiểm tra bằng `mvn -version` |
| MySQL Server | 8.0.16 trở lên; MySQL Workbench giúp chạy file SQL |
| Node.js | 20.19+ hoặc 22.12+ |
| npm | Phiên bản tương thích với Node.js; kiểm tra bằng `npm --version` |
| Git | Dùng để lấy mã nguồn và làm việc theo nhánh |

MySQL cần chạy tại `localhost:3306`. Các lệnh dưới đây dùng PowerShell trên Windows; trên hệ điều hành khác, thay lệnh sao chép file cho phù hợp.

## Cài đặt và chạy

### 1. Lấy mã nguồn

```powershell
git clone https://github.com/hbao06/wedding-management.git
cd wedding-management
git switch feature/project-setup
```

Khung mã nguồn đang được phát triển trên `feature/project-setup`. Sau khi nhánh này được merge vào `develop`, có thể chuyển sang `develop` để chạy phiên bản đã tích hợp.

### 2. Tạo cơ sở dữ liệu

1. Mở MySQL Workbench và kết nối MySQL Server bằng tài khoản trên máy của bạn.
2. Chọn **File > Open SQL Script**, mở `database/schema.sql`, rồi chạy toàn bộ script (nút tia sét).
3. Mở `database/seed.sql` và chạy tương tự.
4. Kiểm tra bằng một tab SQL mới:

```sql
SHOW TABLES FROM wedding_db;
SELECT COUNT(*) AS so_sanh FROM wedding_db.SANH;
```

Kết quả mong đợi là **9 bảng** và `so_sanh = 5`. `schema.sql` không xóa database hiện có; `seed.sql` có thể chạy lại mà không thêm trùng các bản ghi mẫu.

### 3. Chạy Backend

Mở PowerShell tại thư mục gốc repo:

```powershell
cd backend
Copy-Item .env.example .env
```

Mở `backend/.env` và thay `DB_PASSWORD` bằng mật khẩu MySQL trên máy. Giá trị mặc định là `DB_URL=jdbc:mysql://localhost:3306/wedding_db` và `DB_USERNAME=root`; sửa nếu cấu hình MySQL của bạn khác. **Không commit hoặc chia sẻ `.env` và mật khẩu.**

Chạy lệnh sau khi PowerShell đang ở thư mục `backend` (có `pom.xml`):

```powershell
mvn spring-boot:run
```

Giữ cửa sổ này mở. Truy cập <http://localhost:8080/api/health>; khi kết nối thành công, phản hồi có dạng:

```json
{"status":"UP","database":"UP","soSanh":5}
```

Backend dùng `spring.jpa.hibernate.ddl-auto=validate`: Hibernate chỉ kiểm tra cấu trúc bảng, không tự tạo schema. Nếu chạy từ IntelliJ IDEA, đặt **Working directory** là thư mục `backend` để ứng dụng đọc được `.env`.

### 4. Chạy Frontend

Mở **PowerShell thứ hai** tại thư mục gốc repo:

```powershell
cd frontend
npm install
npm run dev
```

Mở địa chỉ Vite in ra trong PowerShell, thường là <http://localhost:5173>. Trang Tổng quan sẽ gọi `/api/health` qua proxy Vite. Khi Backend và MySQL đang chạy, trang hiển thị Backend **Đang chạy**, CSDL **Đã kết nối** và **5 sảnh**. Nếu cổng 5173 đã được dùng, mở đúng địa chỉ Vite in ra thay vì đoán cổng.

Frontend mặc định gọi `/api` qua proxy tới `localhost:8080`. Nếu Backend chạy cổng khác, sửa `server.proxy['/api']` trong `frontend/vite.config.js` rồi khởi động lại Vite.

### 5. Chạy kiểm thử và build

Tại `backend/`:

```powershell
mvn test
```

Tại `frontend/`:

```powershell
npm run build
```

`npm run build` kiểm tra Frontend biên dịch được. `mvn test` hiện chưa chạy test case nghiệp vụ vì thư mục test chưa được bổ sung. Để xác nhận luồng tích hợp, kiểm tra `/api/health` và trang Tổng quan như hai bước trên; chỉ ghi kết quả vào báo cáo sau khi chạy thực tế trên máy.

## Tài khoản mẫu

`database/seed.sql` **chưa tạo tài khoản đăng nhập**; chức năng đăng nhập thuộc nhánh `feature/login`. Các tên tài khoản dưới đây là dự kiến cho dữ liệu mẫu ở giai đoạn sau, hiện **không thể dùng để đăng nhập**:

| Tên dự kiến | Vai trò |
| --- | --- |
| `sales1` | Nhân viên Sales |
| `ketoan1` | Kế toán |
| `quanly1` | Quản lý nhà hàng |
| `admin` | Quản trị hệ thống |

Không đưa mật khẩu mẫu hoặc mật khẩu thật vào README.

## API

API có tiền tố `/api`. Trên nhánh `feature/project-setup`, **chỉ endpoint đầu tiên đã có code**; các endpoint còn lại là danh sách dự kiến theo thiết kế Lab 6.

| Trạng thái | Phương thức | Đường dẫn | Mục đích |
| --- | --- | --- | --- |
| Đã có | `GET` | `/api/health` | Trạng thái Backend, kết nối CSDL và số sảnh |
| Dự kiến | `POST` | `/api/auth/login` | Đăng nhập |
| Dự kiến | `GET` | `/api/sanh` | Danh sách sảnh |
| Dự kiến | `GET` | `/api/sanh/con-trong?ngay=2026-12-20&ca=TOI` | Kiểm tra sảnh trống |
| Dự kiến | `GET` | `/api/dich-vu` | Danh sách dịch vụ |
| Dự kiến | `POST` | `/api/phieu` | Lập phiếu đặt tiệc |
| Dự kiến | `GET` | `/api/phieu/{maPhieu}` | Xem phiếu đặt tiệc |
| Dự kiến | `GET` | `/api/thanh-toan/phieu?q=` | Tra cứu phiếu thanh toán |
| Dự kiến | `GET` | `/api/thanh-toan/phieu/{maPhieu}/xem-truoc?ngayThanhToan=` | Xem trước số tiền cần thanh toán |
| Dự kiến | `POST` | `/api/thanh-toan/hoa-don` | Lập hóa đơn thanh toán |

## Quy trình làm việc nhóm

1. Cập nhật `develop`, tạo nhánh `feature/*` cho phần việc được giao.
2. Commit thay đổi nhỏ, rõ mục đích; không commit `.env`, mật khẩu, `node_modules/`, `target/` hoặc `dist/`.
3. Chạy các bước kiểm tra liên quan, đẩy nhánh lên GitHub và tạo Pull Request vào `develop`.
4. Thành viên khác review PR; sau khi tích hợp và kiểm tra, `develop` được merge vào `main` qua PR.

Xem [docs/git-flow.md](docs/git-flow.md) để biết người review từng nhánh và quy tắc chi tiết.
