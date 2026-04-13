-- This script runs only on first initialization when mysql_data volume is empty.
-- Keep this file idempotent and safe to run on clean environments.

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS shiftmanage
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE shiftmanage;
ALTER DATABASE shiftmanage CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS users (
  userid INT NOT NULL AUTO_INCREMENT,
  full_name NVARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  role ENUM('ADMIN','USER') NOT NULL,
  status BIT NOT NULL,
  PRIMARY KEY (userid),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS shifts (
  shiftid INT NOT NULL AUTO_INCREMENT,
  shift_name NVARCHAR(100) NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  PRIMARY KEY (shiftid)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS work_schedules (
  scheduleid INT NOT NULL AUTO_INCREMENT,
  userid INT NOT NULL,
  shiftid INT NOT NULL,
  work_date DATE NOT NULL,
  PRIMARY KEY (scheduleid),
  UNIQUE KEY uk_work_schedule_user_date_shift (userid, work_date, shiftid),
  CONSTRAINT fk_work_schedules_user FOREIGN KEY (userid) REFERENCES users (userid),
  CONSTRAINT fk_work_schedules_shift FOREIGN KEY (shiftid) REFERENCES shifts (shiftid)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS attendance (
  attendanceid INT NOT NULL AUTO_INCREMENT,
  userid INT NOT NULL,
  work_date DATE NOT NULL,
  check_in DATETIME NULL,
  check_out DATETIME NULL,
  status NVARCHAR(20) NOT NULL,
  PRIMARY KEY (attendanceid),
  UNIQUE KEY uk_attendance_user_date (userid, work_date),
  CONSTRAINT fk_attendance_user FOREIGN KEY (userid) REFERENCES users (userid)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS requests (
  requestid INT NOT NULL AUTO_INCREMENT,
  userid INT NOT NULL,
  type NVARCHAR(20) NOT NULL,
  from_date DATE NULL,
  to_date DATE NULL,
  reason NVARCHAR(500) NOT NULL,
  status ENUM('PENDING','APPROVED','REJECTED') NOT NULL,
  created_at DATETIME NOT NULL,
  work_date DATE NULL,
  shiftid INT NULL,
  target_shiftid INT NULL,
  PRIMARY KEY (requestid),
  CONSTRAINT fk_requests_user FOREIGN KEY (userid) REFERENCES users (userid),
  CONSTRAINT fk_requests_shift FOREIGN KEY (shiftid) REFERENCES shifts (shiftid),
  CONSTRAINT fk_requests_target_shift FOREIGN KEY (target_shiftid) REFERENCES shifts (shiftid)
) ENGINE=InnoDB;

-- ===== SAMPLE DATA FOR 5 ENTITIES =====
-- users
INSERT INTO users (full_name, email, password, role, phone, status)
SELECT 'Quản trị viên', 'admin@company.com', '123456', 'ADMIN', '0900000001', 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@company.com');

INSERT INTO users (full_name, email, password, role, phone, status)
SELECT 'Nguyễn Văn An', 'employee1@company.com', '123456', 'USER', '0900000002', 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'employee1@company.com');

INSERT INTO users (full_name, email, password, role, phone, status)
SELECT 'Trần Thị Bình', 'employee2@company.com', '123456', 'USER', '0900000003', 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'employee2@company.com');

INSERT INTO users (full_name, email, password, role, phone, status)
SELECT 'Lê Quốc Cường', 'employee3@company.com', '123456', 'USER', '0900000004', 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'employee3@company.com');

-- shifts
INSERT INTO shifts (shift_name, start_time, end_time)
SELECT 'Ca sáng', '08:00:00', '12:00:00'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE shift_name = 'Ca sáng');

INSERT INTO shifts (shift_name, start_time, end_time)
SELECT 'Ca chiều', '13:00:00', '17:00:00'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE shift_name = 'Ca chiều');

INSERT INTO shifts (shift_name, start_time, end_time)
SELECT 'Ca tối', '18:00:00', '22:00:00'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE shift_name = 'Ca tối');

-- work_schedules
INSERT INTO work_schedules (userid, shiftid, work_date)
SELECT u.userid, s.shiftid, CURDATE()
FROM users u
JOIN shifts s ON s.shift_name = 'Ca sáng'
WHERE u.email = 'employee1@company.com'
  AND NOT EXISTS (
    SELECT 1 FROM work_schedules ws
    WHERE ws.userid = u.userid AND ws.shiftid = s.shiftid AND ws.work_date = CURDATE()
  );

INSERT INTO work_schedules (userid, shiftid, work_date)
SELECT u.userid, s.shiftid, CURDATE()
FROM users u
JOIN shifts s ON s.shift_name = 'Ca chiều'
WHERE u.email = 'employee2@company.com'
  AND NOT EXISTS (
    SELECT 1 FROM work_schedules ws
    WHERE ws.userid = u.userid AND ws.shiftid = s.shiftid AND ws.work_date = CURDATE()
  );

INSERT INTO work_schedules (userid, shiftid, work_date)
SELECT u.userid, s.shiftid, DATE_ADD(CURDATE(), INTERVAL 1 DAY)
FROM users u
JOIN shifts s ON s.shift_name = 'Ca tối'
WHERE u.email = 'employee3@company.com'
  AND NOT EXISTS (
    SELECT 1 FROM work_schedules ws
    WHERE ws.userid = u.userid AND ws.shiftid = s.shiftid AND ws.work_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)
  );

-- attendance
INSERT INTO attendance (userid, work_date, check_in, check_out, status)
SELECT u.userid,
       DATE_SUB(CURDATE(), INTERVAL 1 DAY),
       TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:03:00'),
       TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '17:01:00'),
       'TRE'
FROM users u
WHERE u.email = 'employee1@company.com'
  AND NOT EXISTS (
    SELECT 1 FROM attendance a
    WHERE a.userid = u.userid AND a.work_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
  );

INSERT INTO attendance (userid, work_date, check_in, check_out, status)
SELECT u.userid,
       DATE_SUB(CURDATE(), INTERVAL 1 DAY),
       TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '12:58:00'),
       TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '17:05:00'),
       'DI_LAM'
FROM users u
WHERE u.email = 'employee2@company.com'
  AND NOT EXISTS (
    SELECT 1 FROM attendance a
    WHERE a.userid = u.userid AND a.work_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
  );

INSERT INTO attendance (userid, work_date, check_in, check_out, status)
SELECT u.userid, DATE_SUB(CURDATE(), INTERVAL 1 DAY), NULL, NULL, 'NGHI'
FROM users u
WHERE u.email = 'employee3@company.com'
  AND NOT EXISTS (
    SELECT 1 FROM attendance a
    WHERE a.userid = u.userid AND a.work_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
  );

-- requests
INSERT INTO requests (userid, type, from_date, to_date, reason, status, created_at, work_date, shiftid, target_shiftid)
SELECT u.userid, 'LEAVE', '2026-04-20', '2026-04-20', 'Xin nghỉ khám sức khỏe định kỳ', 'APPROVED', '2026-04-10 09:00:00', NULL, NULL, NULL
FROM users u
WHERE u.email = 'employee2@company.com'
  AND NOT EXISTS (
    SELECT 1 FROM requests r
    WHERE r.userid = u.userid AND r.type = 'LEAVE' AND r.from_date = '2026-04-20' AND r.to_date = '2026-04-20'
  );

INSERT INTO requests (userid, type, from_date, to_date, reason, status, created_at, work_date, shiftid, target_shiftid)
SELECT u.userid, 'SHIFT_CHANGE', NULL, NULL, 'Xin đổi ca do bận việc gia đình', 'PENDING', '2026-04-12 18:00:00', '2026-04-15', sCurrent.shiftid, sTarget.shiftid
FROM users u
JOIN shifts sCurrent ON sCurrent.shift_name = 'Ca tối'
JOIN shifts sTarget ON sTarget.shift_name = 'Ca sáng'
WHERE u.email = 'employee3@company.com'
  AND NOT EXISTS (
    SELECT 1 FROM requests r
    WHERE r.userid = u.userid AND r.type = 'SHIFT_CHANGE' AND r.work_date = '2026-04-15'
  );
