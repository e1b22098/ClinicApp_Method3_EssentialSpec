-- 診療予約管理システム データベーススキーマ
-- スキーマ名: clinic_booking_db

DROP DATABASE IF EXISTS clinic_booking_db;
CREATE DATABASE clinic_booking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clinic_booking_db;

-- 患者テーブル
CREATE TABLE patients (
    patient_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '患者ID（主キー）',
    patient_number VARCHAR(20) NOT NULL UNIQUE COMMENT '診察券番号',
    name VARCHAR(100) NOT NULL COMMENT '氏名',
    birth_date DATE NOT NULL COMMENT '生年月日',
    phone_number VARCHAR(20) NOT NULL COMMENT '電話番号',
    password VARCHAR(255) NOT NULL COMMENT 'パスワード（ハッシュ化）',
    reset_token VARCHAR(255) NULL COMMENT 'パスワードリセットトークン',
    reset_token_expiry DATETIME NULL COMMENT 'リセットトークン有効期限',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    INDEX idx_patient_number (patient_number),
    INDEX idx_reset_token (reset_token)
) COMMENT='患者マスタ';

-- 管理者テーブル
CREATE TABLE administrators (
    admin_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '管理者ID（主キー）',
    admin_login_id VARCHAR(50) NOT NULL UNIQUE COMMENT '管理者ログインID',
    password VARCHAR(255) NOT NULL COMMENT 'パスワード（ハッシュ化）',
    name VARCHAR(100) NOT NULL COMMENT '管理者名',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    INDEX idx_admin_login_id (admin_login_id)
) COMMENT='管理者マスタ';

-- 営業日テーブル
CREATE TABLE business_days (
    business_day_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '営業日ID（主キー）',
    business_date DATE NOT NULL UNIQUE COMMENT '営業日',
    is_available BOOLEAN NOT NULL DEFAULT TRUE COMMENT '予約受付可否（TRUE:受付中、FALSE:停止）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    INDEX idx_business_date (business_date),
    INDEX idx_is_available (is_available)
) COMMENT='営業日マスタ';

-- 予約テーブル
CREATE TABLE bookings (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '予約ID（主キー）',
    patient_id BIGINT NOT NULL COMMENT '患者ID（外部キー）',
    business_date DATE NOT NULL COMMENT '予約日',
    time_slot VARCHAR(20) NOT NULL COMMENT '時間枠（例: 09:00-09:30）',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '予約ステータス（PENDING:確認待ち、CONFIRMED:確定、CANCELLED:キャンセル）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    INDEX idx_patient_id (patient_id),
    INDEX idx_business_date (business_date),
    INDEX idx_status (status),
    UNIQUE KEY uk_booking_date_time (business_date, time_slot, status)
) COMMENT='予約テーブル';

-- 初期データ（管理者）
INSERT INTO administrators (admin_login_id, password, name) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJDm', 'システム管理者');
-- パスワード: admin

-- 初期データ（営業日サンプル - 次月の営業日を3件追加）
INSERT INTO business_days (business_date, is_available) VALUES
(DATE_ADD(CURDATE(), INTERVAL 1 MONTH), TRUE),
(DATE_ADD(CURDATE(), INTERVAL 1 MONTH) + INTERVAL 1 DAY, TRUE),
(DATE_ADD(CURDATE(), INTERVAL 1 MONTH) + INTERVAL 2 DAY, TRUE);
