-- rolesテーブル作成
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- usersテーブル作成
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role_id INT NOT NULL REFERENCES roles(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('active', 'retired')),
    email VARCHAR(255) NOT NULL
);

-- login_credentialsテーブル作成
CREATE TABLE login_credentials (
    user_id INT PRIMARY KEY REFERENCES users(id),
    password_hash VARCHAR(255) NOT NULL,
    created_date DATE NOT NULL,
    edited_date DATE NOT NULL
);

-- roles 初期データ投入
INSERT INTO roles (id, name) VALUES (1, 'admin');
INSERT INTO roles (id, name) VALUES (2, 'user');
INSERT INTO roles (id, name) VALUES (3, 'viewer');

-- users 初期データ投入
INSERT INTO users (name, role_id, status, email)
VALUES ('Test User', 1, 'active', 'testuser@example.com');

-- login_credentials 初期データ投入
INSERT INTO login_credentials (user_id, password_hash, created_date, edited_date)
VALUES (
    (SELECT id FROM users WHERE email = 'testuser@example.com'),
    '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8',
    CURRENT_DATE,
    CURRENT_DATE
);