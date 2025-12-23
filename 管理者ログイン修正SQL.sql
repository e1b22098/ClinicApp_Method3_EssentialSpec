-- 管理者ログイン問題の修正SQL
-- データベースに接続して実行してください

USE clinic_booking_db;

-- 1. 現在の管理者レコードを確認
SELECT * FROM administrators WHERE admin_login_id = 'admin';

-- 2. 管理者レコードが存在しない場合、新規作成
-- パスワードハッシュは PasswordHashGenerator.java を実行して生成してください
INSERT INTO administrators (admin_login_id, password, name) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJDm', 'システム管理者')
ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    name = VALUES(name);

-- 3. 確認
SELECT admin_login_id, name, LEFT(password, 20) as password_hash_preview FROM administrators WHERE admin_login_id = 'admin';
