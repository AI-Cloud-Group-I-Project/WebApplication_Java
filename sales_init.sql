DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS inventories;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS product_statuses;
DROP TABLE IF EXISTS weathers;

-- product_statusesテーブル作成
CREATE TABLE product_statuses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL CHECK (name IN ('available', 'discontinued'))
);

-- product_statuses 初期データ投入
INSERT INTO product_statuses (name) VALUES ('available'), ('discontinued');

-- productsテーブル作成
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    jan_code VARCHAR(20) NOT NULL UNIQUE,
    status_id INT NOT NULL,
    CONSTRAINT fk_status FOREIGN KEY (status_id) REFERENCES product_statuses(id)
);

-- weathersテーブル作成（← date は DATE のまま）
CREATE TABLE weathers (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    average_temperature FLOAT NOT NULL,
    precipitation FLOAT NOT NULL,
    weather_condition VARCHAR(50) NOT NULL
);

-- salesテーブル作成（DATE に戻した版）
CREATE TABLE sales (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    weathers_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0),
    sales_date DATE NOT NULL,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    edited_date DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT fk_sales_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_sales_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_sales_weathers FOREIGN KEY (weathers_id) REFERENCES weathers(id)
);

-- inventoriesテーブル作成（TIMESTAMP＋デフォルト値）
CREATE TABLE inventories (
    product_id INT PRIMARY KEY,
    stock_quantity INT NOT NULL CHECK (stock_quantity >= 0),
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- products 初期データ投入
INSERT INTO products (name, price, jan_code, status_id)
VALUES 
  ('ホワイトビール', 900, '4901234567894', 1),
  ('ラガー', 800, '4512345678907', 1),
  ('ペールエール', 1000, '4987654321097', 1),
  ('フルーツビール', 1000, '4545678901234', 1),
  ('黒ビール', 1200, '4999999999996', 1),
  ('IPA', 900, '4571234567892', 1);

-- inventories 初期データ投入
INSERT INTO inventories (product_id, stock_quantity, updated_date)
VALUES
  (1, 25, '2025-06-02 00:00:00'),
  (2, 30, '2025-06-03 00:00:00'),
  (3, 18, '2025-06-04 00:00:00'),
  (4, 22, '2025-06-05 00:00:00'),
  (5, 15, '2025-06-06 00:00:00'),
  (6, 28, '2025-06-01 00:00:00');
