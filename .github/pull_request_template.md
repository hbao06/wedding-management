## Tóm tắt

<!-- Mô tả ngắn gọn PR này làm gì trong 1-2 câu. -->

## Liên quan

<!-- Link issue / user story / backlog item. Ví dụ: Closes #12, US02-QĐ1. -->

## Thay đổi chính

<!-- Liệt kê chức năng và các tệp/thư mục quan trọng đã thêm hoặc sửa. -->

-
-

## Cách kiểm thử

<!-- Các bước để reviewer kiểm tra lại. -->

1.
2.

**Kết quả kiểm thử:**

- [ ] Backend: `mvn test` — BUILD SUCCESS
- [ ] Frontend: `npm run build` — thành công
- [ ] Đã kiểm tra thủ công luồng liên quan

## Ảnh minh chứng

<!-- Dán ảnh giao diện hoặc kết quả test nếu có. -->

## Lưu ý cho reviewer

<!-- Rủi ro, cấu hình cần thiết (.env, seed data), điểm cần chú ý khi review. -->

## Checklist

- [ ] Đúng phạm vi yêu cầu, không lẫn thay đổi ngoài lề
- [ ] Tên biến, hàm, lớp rõ ràng, tuân thủ convention dự án
- [ ] Không còn mã thừa, comment debug hoặc `console.log`
- [ ] Không commit `.env`, mật khẩu, dữ liệu thật, `node_modules/`, `target/`, `dist/`
- [ ] API lỗi trả về đúng định dạng `{ "message": "...", "details": [...] }`
