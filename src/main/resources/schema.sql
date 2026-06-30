-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    telegram_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы конфигурации OTP
CREATE TABLE IF NOT EXISTS otp_config (
    id BIGSERIAL PRIMARY KEY,
    ttl_seconds INTEGER NOT NULL DEFAULT 300,
    code_length INTEGER NOT NULL DEFAULT 6,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Вставка начальной конфигурации
INSERT INTO otp_config (ttl_seconds, code_length)
SELECT 300, 6
WHERE NOT EXISTS (SELECT 1 FROM otp_config);

-- Создание таблицы OTP кодов
CREATE TABLE IF NOT EXISTS otp_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    operation_id VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    delivery_channel VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Создание индексов для оптимизации
CREATE INDEX idx_otp_codes_code_operation ON otp_codes(code, operation_id);
CREATE INDEX idx_otp_codes_user_id ON otp_codes(user_id);
CREATE INDEX idx_otp_codes_status_expires ON otp_codes(status, expires_at);
CREATE INDEX idx_users_username ON users(username);