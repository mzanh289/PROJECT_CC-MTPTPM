-- =========================================================
-- Sample seed data for current entities:
-- users, shifts, work_schedules, attendance, requests
-- This script is idempotent: safe to run many times.
-- =========================================================

-- 1) USERS
INSERT INTO users (full_name, email, password, role, phone, status)
SELECT 'Quản trị viên', 'admin@company.com', '123456', 'ADMIN', '0900000001', 1
WHERE NOT EXISTS (
	SELECT 1 FROM users WHERE email = 'admin@company.com'
);

INSERT INTO users (full_name, email, password, role, phone, status)
SELECT 'Nguyễn Văn An', 'employee1@company.com', '123456', 'USER', '0900000002', 1
WHERE NOT EXISTS (
	SELECT 1 FROM users WHERE email = 'employee1@company.com'
);

INSERT INTO users (full_name, email, password, role, phone, status)
SELECT 'Trần Thị Bình', 'employee2@company.com', '123456', 'USER', '0900000003', 1
WHERE NOT EXISTS (
	SELECT 1 FROM users WHERE email = 'employee2@company.com'
);

INSERT INTO users (full_name, email, password, role, phone, status)
SELECT 'Lê Quốc Cường', 'employee3@company.com', '123456', 'USER', '0900000004', 1
WHERE NOT EXISTS (
	SELECT 1 FROM users WHERE email = 'employee3@company.com'
);

-- 2) SHIFTS
INSERT INTO shifts (shift_name, start_time, end_time)
SELECT 'Ca sáng', '08:00:00', '12:00:00'
WHERE NOT EXISTS (
	SELECT 1 FROM shifts WHERE shift_name = 'Ca sáng'
);

INSERT INTO shifts (shift_name, start_time, end_time)
SELECT 'Ca chiều', '13:00:00', '17:00:00'
WHERE NOT EXISTS (
	SELECT 1 FROM shifts WHERE shift_name = 'Ca chiều'
);

INSERT INTO shifts (shift_name, start_time, end_time)
SELECT 'Ca tối', '18:00:00', '22:00:00'
WHERE NOT EXISTS (
	SELECT 1 FROM shifts WHERE shift_name = 'Ca tối'
);

-- 3) WORK SCHEDULES (unique by UserID + WorkDate + ShiftID)
INSERT INTO work_schedules (userid, shiftid, work_date)
SELECT u.userid, s.shiftid, CURDATE()
FROM users u
JOIN shifts s ON s.shift_name = 'Ca sáng'
WHERE u.email = 'employee1@company.com'
	AND NOT EXISTS (
		SELECT 1
		FROM work_schedules ws
		WHERE ws.userid = u.userid
			AND ws.shiftid = s.shiftid
			AND ws.work_date = CURDATE()
	);

INSERT INTO work_schedules (userid, shiftid, work_date)
SELECT u.userid, s.shiftid, CURDATE()
FROM users u
JOIN shifts s ON s.shift_name = 'Ca chiều'
WHERE u.email = 'employee2@company.com'
	AND NOT EXISTS (
		SELECT 1
		FROM work_schedules ws
		WHERE ws.userid = u.userid
			AND ws.shiftid = s.shiftid
			AND ws.work_date = CURDATE()
	);

INSERT INTO work_schedules (userid, shiftid, work_date)
SELECT u.userid, s.shiftid, DATE_ADD(CURDATE(), INTERVAL 1 DAY)
FROM users u
JOIN shifts s ON s.shift_name = 'Ca tối'
WHERE u.email = 'employee3@company.com'
	AND NOT EXISTS (
		SELECT 1
		FROM work_schedules ws
		WHERE ws.userid = u.userid
			AND ws.shiftid = s.shiftid
			AND ws.work_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)
	);

-- 4) ATTENDANCE (unique by UserID + WorkDate)
INSERT INTO attendance (userid, work_date, check_in, check_out, status)
SELECT u.userid,
       DATE_SUB(CURDATE(), INTERVAL 1 DAY),
       TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:03:00'),
       TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '17:01:00'),
       'TRE'
FROM users u
WHERE u.email = 'employee1@company.com'
	AND NOT EXISTS (
		SELECT 1
		FROM attendance a
		WHERE a.userid = u.userid
			AND a.work_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
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
		SELECT 1
		FROM attendance a
		WHERE a.userid = u.userid
			AND a.work_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
	);

INSERT INTO attendance (userid, work_date, check_in, check_out, status)
SELECT u.userid, DATE_SUB(CURDATE(), INTERVAL 1 DAY), NULL, NULL, 'NGHI'
FROM users u
WHERE u.email = 'employee3@company.com'
	AND NOT EXISTS (
		SELECT 1
		FROM attendance a
		WHERE a.userid = u.userid
			AND a.work_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
	);

-- 5) REQUESTS
INSERT INTO requests (userid, type, from_date, to_date, reason, status, created_at, work_date, shiftid, target_shiftid)
SELECT u.userid, 'LEAVE', '2026-04-20', '2026-04-20', 'Xin nghỉ khám sức khỏe định kỳ', 'APPROVED', '2026-04-10 09:00:00', NULL, NULL, NULL
FROM users u
WHERE u.email = 'employee2@company.com'
	AND NOT EXISTS (
		SELECT 1
		FROM requests r
		WHERE r.userid = u.userid
			AND r.type = 'LEAVE'
			AND r.from_date = '2026-04-20'
			AND r.to_date = '2026-04-20'
	);

INSERT INTO requests (userid, type, from_date, to_date, reason, status, created_at, work_date, shiftid, target_shiftid)
SELECT u.userid, 'SHIFT_CHANGE', NULL, NULL, 'Xin đổi ca do bận việc gia đình', 'PENDING', '2026-04-12 18:00:00', '2026-04-15', sCurrent.shiftid, sTarget.shiftid
FROM users u
JOIN shifts sCurrent ON sCurrent.shift_name = 'Ca tối'
JOIN shifts sTarget ON sTarget.shift_name = 'Ca sáng'
WHERE u.email = 'employee3@company.com'
	AND NOT EXISTS (
		SELECT 1
		FROM requests r
		WHERE r.userid = u.userid
			AND r.type = 'SHIFT_CHANGE'
			AND r.work_date = '2026-04-15'
	);

