# Shift Management - Docker, Cypress, Render

README này tập trung vào 3 nhu cầu chính:
1. Chạy local bằng Docker
2. Chạy Cypress UI và E2E test
3. Cấu hình để deploy lên Render

## 1. Yêu cầu môi trường

- Docker Desktop
- Node.js 18+
- npm
- Git

## 2. Chạy ứng dụng bằng Docker (Local)

### Bước 1: Tạo file môi trường

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

macOS/Linux:

```bash
cp .env.example .env
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

### Bước 2: Khởi động hệ thống

```bash
docker-compose up -d
```

Kiểm tra:
- App: http://localhost:8081
- Login: http://localhost:8081/login
- MySQL host: localhost:3307

### Bước 3: Dừng hệ thống

```bash
docker-compose down
```

### Reset sạch DB (nếu cần)

```bash
docker-compose down -v
docker-compose up --build -d
```

Lưu ý:
- `init.sql` chỉ chạy khi volume DB mới được tạo.
- `down -v` sẽ xóa toàn bộ dữ liệu DB local.

## 3. Chạy Cypress UI và E2E test

### Cài đặt dependencies

```bash
npm install
```

### Mở giao diện Cypress (UI)

```bash
npm run cy:open
```

Trong Cypress:
1. Chọn E2E Testing
2. Chọn browser (Chrome/Edge)
3. Chọn file test để chạy

Spec hiện có:
- `cypress/e2e/01-auth/auth.cy.js`
- `cypress/e2e/02-admin/users.cy.js`
- `cypress/e2e/03-employee/requests.cy.js`
- `cypress/e2e/04-rbac/rbac.cy.js`

### Chạy headless

```bash
npm run cy:run
```

### Chạy headless có video

```bash
npm run cy:run:video
```

### Chạy full flow tự động trên Windows (build Docker + wait + test + down)

```bash
npm run e2e:video
```

Script PowerShell sẽ:
1. `docker-compose up --build -d`
2. Chờ endpoint `http://localhost:8081/login` sẵn sàng
3. Chạy Cypress headless với `video=true`
4. `docker-compose down`

Artifacts:
- Video: `cypress/videos`
- Screenshot khi fail: `cypress/screenshots`

## 4. Cấu hình deploy lên Render

Project đã sẵn sàng ở các điểm sau:
- Server dùng cổng động từ Render: `server.port=${PORT:8081}`
- Datasource dùng env vars:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- Đã có `Dockerfile` để build và run app

### Cách deploy khuyến nghị (Render Web Service với Docker)

1. Push code lên GitHub
2. Trên Render: New + -> Web Service
3. Connect repo, chọn branch
4. Environment: Docker
5. Render sẽ build bằng `Dockerfile`
6. Set Environment Variables trên Render:

Bắt buộc:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Khuyến nghị:
- `SPRING_SQL_INIT_MODE=never`
- `TZ=Asia/Ho_Chi_Minh`
- `JAVA_TOOL_OPTIONS=-Duser.timezone=Asia/Ho_Chi_Minh`

Lưu ý quan trọng khi deploy:
- Không cần set `PORT` thủ công trên Render (Render tự cấp).
- Không dùng `docker-compose.yml` để chạy production trên Render.
- Không commit `.env` lên repository.

## 5. Kiểm tra sau deploy

Sau khi deploy thành công:
1. Mở URL service trên Render
2. Truy cập `/login`
3. Đăng nhập và kiểm tra nhanh các màn hình chính

Nếu muốn chạy Cypress vào môi trường Render (staging/prod), có thể override `baseUrl`:

```bash
npx cypress open --config baseUrl=https://your-service.onrender.com
```
