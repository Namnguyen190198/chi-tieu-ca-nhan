# Chi tiêu Offline

Ứng dụng Android quản lý thu/chi offline, không cần tài khoản và không cần Internet.

## Build bằng GitHub Actions

1. Tạo repository GitHub mới.
2. Upload toàn bộ nội dung thư mục này (không upload file ZIP bên trong repository).
3. Vào tab **Actions**.
4. Chọn **Build Offline Expense APK**.
5. Chọn **Run workflow**.
6. Khi chạy xong, mở workflow run và tải artifact **app-release**.
7. Giải nén artifact để lấy `app-release.apk`.

Lưu ý: APK trong workflow hiện được build unsigned. Android có thể cài APK unsigned tùy cách đóng gói; nếu thiết bị từ chối, có thể bổ sung bước ký APK bằng keystore.
