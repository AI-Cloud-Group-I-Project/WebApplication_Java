
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL
);

INSERT INTO users (username, password_hash) VALUES
('user1', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8');
