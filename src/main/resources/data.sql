-- Insert initial users if not exist
INSERT IGNORE INTO users (email, full_name, password, phone, role, status) VALUES 
('admin@admin.com', 'Admin User', '123456', '0123456789', 'ADMIN', true),
('user@user.com', 'Test Employee', '123456', '0987654321', 'USER', true);

