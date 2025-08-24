-- symbol 테이블 더미 데이터
INSERT INTO symbol (base_coin, quote_coin, symbol) VALUES
('BTC', 'USDT', 'BTC/USDT'),
('ETH', 'USDT', 'ETH/USDT'),
('ADA', 'USDT', 'ADA/USDT'),
('XRP', 'USDT', 'XRP/USDT');

-- user 테이블 더미 데이터
INSERT INTO user (created_at, updated_at, email, password_hash, balance) VALUES
(NOW(), NOW(), 'user1@example.com', '$2a$10$exampleHash1', 1,000,000),
(NOW(), NOW(), 'user2@example.com', '$2a$10$exampleHash2', 2,000,000),
(NOW(), NOW(), 'user3@example.com', '$2a$10$exampleHash3', 3,000,000);