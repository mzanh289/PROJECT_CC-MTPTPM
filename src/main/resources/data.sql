-- Insert initial users if not exist
INSERT IGNORE INTO users (email, full_name, password, phone, role, status) VALUES 
('admin@admin.com', 'Admin User', '123456', '0123456789', 'ADMIN', true),
('user@user.com', 'Test Employee', '123456', '0987654321', 'USER', true);

-- Insert common shifts if not exist
INSERT IGNORE INTO shifts (shift_name, start_time, end_time) VALUES 
('Ca sáng', '06:00:00', '14:00:00'),
('Ca chiều', '14:00:00', '22:00:00'),
('Ca tối', '22:00:00', '06:00:00');
