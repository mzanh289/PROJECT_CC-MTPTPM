# Shift Management - Local, Cypress, Render

README nay tap trung vao 3 nhu cau chinh:
1. Chay local bang Docker
2. Chay Cypress UI va E2E test
3. Cau hinh de deploy len Render

## 1. Yeu cau moi truong

- Docker Desktop
- Node.js 18+
- npm
- Git

## 2. Chay ung dung bang Docker (Local)

### Buoc 1: Tao file moi truong

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

macOS/Linux:

```bash
cp .env.example .env
```

Noi dung bien quan trong trong `.env`:

```properties
APP_PORT=8081
APP_TIMEZONE=Asia/Ho_Chi_Minh
MYSQL_HOST_PORT=3307
MYSQL_DATABASE=shiftmanage
MYSQL_USER=shift_user
MYSQL_PASSWORD=shift_pass
MYSQL_ROOT_PASSWORD=root123
```

### Buoc 2: Khoi dong he thong

```bash
docker-compose up -d
```

Kiem tra:
- App: http://localhost:8081
- Login: http://localhost:8081/login
- MySQL host: localhost:3307

### Buoc 3: Dung he thong

```bash
docker-compose down
```

### Reset sach DB (neu can)

```bash
docker-compose down -v
docker-compose up --build -d
```

Luu y:
- `init.sql` chi chay khi volume DB moi duoc tao.
- `down -v` se xoa toan bo du lieu DB local.

## 3. Chay Cypress UI va E2E test

### Cai dat dependencies

```bash
npm install
```

### Mo giao dien Cypress (UI)

```bash
npm run cy:open
```

Trong Cypress:
1. Chon E2E Testing
2. Chon browser (Chrome/Edge)
3. Chon file test de chay

Spec hien co:
- `cypress/e2e/01-auth/auth.cy.js`
- `cypress/e2e/02-admin/users.cy.js`
- `cypress/e2e/03-employee/requests.cy.js`
- `cypress/e2e/04-rbac/rbac.cy.js`

### Chay headless

```bash
npm run cy:run
```

### Chay headless co video

```bash
npm run cy:run:video
```

### Chay full flow tu dong tren Windows (build Docker + wait + test + down)

```bash
npm run e2e:video
```

Script PowerShell se:
1. `docker-compose up --build -d`
2. Cho endpoint `http://localhost:8081/login` san sang
3. Chay Cypress headless voi `video=true`
4. `docker-compose down`

Artifacts:
- Video: `cypress/videos`
- Screenshot khi fail: `cypress/screenshots`

## 4. Cau hinh deploy len Render

Project da san sang nhung diem sau:
- Server dung cong dong tu Render: `server.port=${PORT:8081}`
- Datasource dung env vars:
	- `SPRING_DATASOURCE_URL`
	- `SPRING_DATASOURCE_USERNAME`
	- `SPRING_DATASOURCE_PASSWORD`
- Da co `Dockerfile` de build va run app

### Cach deploy khuyen nghi (Render Web Service voi Docker)

1. Push code len GitHub
2. Tren Render: New + -> Web Service
3. Connect repo, chon branch
4. Environment: Docker
5. Render se build bang `Dockerfile`
6. Set Environment Variables tren Render:

Bat buoc:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Khuyen nghi:
- `SPRING_SQL_INIT_MODE=never`
- `TZ=Asia/Ho_Chi_Minh`
- `JAVA_TOOL_OPTIONS=-Duser.timezone=Asia/Ho_Chi_Minh`

Luu y quan trong khi deploy:
- Khong can set `PORT` thu cong tren Render (Render tu cap).
- Khong dung `docker-compose.yml` de chay production tren Render.
- Khong commit `.env` len repository.

## 5. Kiem tra sau deploy

Sau khi deploy thanh cong:
1. Mo URL service tren Render
2. Truy cap `/login`
3. Dang nhap va kiem tra nhanh cac man hinh chinh

Neu muon chay Cypress vao moi truong Render (staging/prod), co the override `baseUrl`:

```bash
npx cypress open --config baseUrl=https://your-service.onrender.com
```
