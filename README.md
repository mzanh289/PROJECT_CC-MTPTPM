# 🔄 Workflow làm việc (có lệnh cụ thể)

## 🚀 1. Lần đầu setup project

### Bước 1: Clone project

```bash
git clone <repo-url>
cd <project-folder>
```

### Bước 2: Tạo file môi trường

```bash
cp .env.example .env
```

Windows:

```powershell
Copy-Item .env.example .env
```

Biến quan trọng trong `.env`:

```properties
APP_PORT=8081
APP_TIMEZONE=Asia/Ho_Chi_Minh
MYSQL_HOST_PORT=3307
MYSQL_DATABASE=shiftmanage
MYSQL_USER=shift_user
MYSQL_PASSWORD=shift_pass
MYSQL_ROOT_PASSWORD=root123
```

### Bước 3: Chạy hệ thống

```bash
docker-compose up -d
```

👉 App: http://localhost:8081
👉 MySQL: localhost:3307
👉 `init.sql` sẽ chạy 1 lần để tạo DB + seed data

---

## 🔄 2. Làm việc hằng ngày

### ▶️ Mở hệ thống

```bash
docker-compose up -d
```

### ⏹️ Tắt hệ thống

```bash
docker-compose down
```

👉 Data **không mất** (được lưu trong volume)

---

## 💾 3. Backup database

```bash
docker compose exec -T mysql mysqldump -ushift_user -pshift_pass shiftmanage > backup.sql
```

Commit (nếu cần):

```bash
git add backup.sql
git commit -m "update database backup"
git push
```

---

## ♻️ 4. Restore database

```bash
git pull
docker-compose up -d
docker compose exec -T mysql mysql -ushift_user -pshift_pass shiftmanage < backup.sql
```

---

## 👥 5. Workflow làm việc nhóm

### Khi bạn cập nhật DB

```bash
docker compose exec -T mysql mysqldump -ushift_user -pshift_pass shiftmanage > backup.sql
git add backup.sql
git commit -m "update DB"
git push
```

### Người khác

```bash
git pull
docker-compose up -d
docker compose exec -T mysql mysql -ushift_user -pshift_pass shiftmanage < backup.sql
```

---

## 🧨 6. Reset toàn bộ hệ thống (QUAN TRỌNG)

👉 Dùng khi:

* DB lỗi
* muốn quay về trạng thái ban đầu
* test lại từ đầu

### Bước 1: Xóa container + volume (xóa luôn DB)

```bash
docker-compose down -v
```

### Bước 2: (Tuỳ chọn) Xóa image cũ

```bash
docker image prune -a
```

### Bước 3: Chạy lại từ đầu

```bash
docker-compose up --build -d
```

👉 Kết quả:

* DB reset sạch
* chạy lại `init.sql`
* app build lại

---

## ⚠️ Lưu ý quan trọng

❗ `init.sql` chỉ chạy 1 lần
❗ Data thật nằm trong volume
❗ `down -v` = mất toàn bộ dữ liệu
❗ Muốn share DB → phải backup

---

## 🇻🇳 7. Lỗi tiếng Việt khi insert

Nếu gặp lỗi dữ liệu tiếng Việt bị sai dấu, hệ thống hiện tại đã cấu hình:

* MySQL server dùng `utf8mb4` + `utf8mb4_unicode_ci`
* `init.sql` có `SET NAMES utf8mb4`
* JDBC URL bật `useUnicode=true&characterEncoding=utf8`

Để áp dụng lại hoàn toàn cấu hình charset cho DB cũ, chạy:

```bash
docker-compose down -v
docker-compose up --build -d
```

Và đảm bảo file `docker/mysql/init/init.sql` được lưu bằng UTF-8.

---

## ✅ 8. Nút check-in/check-out không bấm được

Nút sẽ bị disable khi không thỏa điều kiện nghiệp vụ:

* Không có ca làm trong ngày
* Đã check-in hoặc đã check-out trước đó
* Đang ở trạng thái nghỉ (`NGHI`)
* Check-in ngoài khung thời gian cho phép (30 phút trước ca đến hết ca)
* Timezone container lệch so với giờ Việt Nam

Để test lại từ dữ liệu mẫu mới (ngày động theo `CURDATE()`):

```bash
docker-compose down -v
docker-compose up --build -d
```

Với seed hiện tại, tài khoản `employee1@company.com` sẽ có ca trong ngày để test check-in/check-out.
Mặc định timezone đã đặt là `Asia/Ho_Chi_Minh`.
