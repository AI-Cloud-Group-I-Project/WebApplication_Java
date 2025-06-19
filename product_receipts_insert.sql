DROP TABLE IF EXISTS product_receipts;

CREATE TABLE product_receipts (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    received_date DATE NOT NULL,
    edited_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_receipts_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);


-- 前提：products テーブルに ID=1,2 の商品が登録されていること
INSERT INTO product_receipts (product_id, quantity, received_date, edited_date)
VALUES
    (1, 20, '2025-06-13', CURRENT_TIMESTAMP),
    (2, 36, '2025-06-19', CURRENT_TIMESTAMP),
    (3, 17, '2025-06-23', CURRENT_TIMESTAMP),
    (4, 35, '2025-06-19', CURRENT_TIMESTAMP),
    (5, 25, '2025-06-21', CURRENT_TIMESTAMP),
    (6, 20, '2025-06-22', CURRENT_TIMESTAMP);
