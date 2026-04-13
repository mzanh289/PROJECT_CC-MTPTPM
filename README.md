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


## 🧪 7. Cypress E2E (có ghi video)

### Cài đặt một lần

```bash
npm install
```

### Chạy test có ghi video tự động

```bash
npm run e2e:video
```

Script này sẽ:

1. `docker-compose up -d`
2. chờ app sẵn sàng tại `http://localhost:8081/login`
3. chạy Cypress headless với `video=true`
4. tự `docker-compose down` khi xong

### Vị trí artifacts

- Video: `cypress/videos`
- Screenshot khi fail: `cypress/screenshots`

### Mở giao diện Cypress để debug

```bash
npm run cy:open
```
