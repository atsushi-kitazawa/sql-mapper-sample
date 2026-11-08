INSERT INTO users (id, name, email, created_at) VALUES
(1, 'Alice', 'alice@example.com', '2026-01-01 10:00:00'),
(2, 'Bob', 'bob@example.com', '2026-01-02 11:00:00'),
(3, 'Charlie', 'charlie@example.com', '2026-01-03 12:00:00');

INSERT INTO orders (id, user_id, order_code, amount, status, ordered_at) VALUES
(1, 1, 'ORD-001', 1500, 'COMPLETED', '2026-01-01 10:30:00'),
(2, 1, 'ORD-002', 3000, 'PENDING', '2026-01-05 14:00:00'),
(3, 2, 'ORD-003', 4500, 'COMPLETED', '2026-01-02 12:00:00');

ALTER TABLE users ALTER COLUMN id RESTART WITH 10;
ALTER TABLE orders ALTER COLUMN id RESTART WITH 10;
