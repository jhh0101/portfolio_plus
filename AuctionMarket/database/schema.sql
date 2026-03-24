SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS sellers;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS auctions;
DROP TABLE IF EXISTS bids;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS seller_ratings;
-- 1. 회원 (Users)
CREATE TABLE users (
                       user_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email              VARCHAR(100) NOT NULL UNIQUE, -- 로그인 ID
                       password           VARCHAR(255) NULL,
                       nickname           VARCHAR(20) NOT NULL UNIQUE,
                       username           VARCHAR(20) NOT NULL,
                       base_address       VARCHAR(255) NOT NULL,
                       detail_address     VARCHAR(10) NULL,
                       phone              VARCHAR(15) NOT NULL UNIQUE,
                       role               ENUM('USER', 'SELLER', 'ADMIN') DEFAULT 'USER', -- USER, SELLER, ADMIN
                       point              BIGINT NOT NULL DEFAULT 0, -- 가상 화폐
                       avg_rating         DOUBLE NOT NULL DEFAULT 0.0, -- 평균 별점 (캐싱용)
                       status             ENUM('NORMAL', 'SUSPENDED', 'WITHDRAWN') DEFAULT 'NORMAL',
                       suspension_reason  VARCHAR(255) NULL, -- 정지 사유
                       created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE sellers (
                         seller_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id         BIGINT NOT NULL UNIQUE,          -- 신청한 사용자 (1:1 관계)
                         store_name      VARCHAR(50) NOT NULL,            -- 스토어 이름
                         bank_name       VARCHAR(20) NOT NULL,            -- 은행명
                         account_number  VARCHAR(30) NOT NULL,            -- 계좌번호
                         account_holder  VARCHAR(20) NOT NULL,            -- 예금주
                         status          ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELED') DEFAULT 'PENDING',
                         reject_reason   VARCHAR(255),                    -- 반려 사유
                         applied_at      DATETIME DEFAULT CURRENT_TIMESTAMP,

                         FOREIGN KEY (user_id) REFERENCES users(user_id)  -- users 테이블의 PK와 연결
);

-- 카테고리 테이블 생성
CREATE TABLE categories (
                            category_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                            category      VARCHAR(50) NOT NULL, -- 카테고리 이름 (예: 가전, 의류)
                            parent_id     BIGINT NULL,           -- 부모 카테고리 ID (대분류일 경우 NULL)
                            path          VARCHAR(100) NULL,  -- 경로 저장
                            is_deleted    TINYINT(1) DEFAULT 0,
                            FOREIGN KEY (parent_id) REFERENCES categories(category_id)
);

-- 2. 상품 (Products)
CREATE TABLE products (
                          product_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                          seller_id       BIGINT NOT NULL,
                          category_id     BIGINT NOT NULL,
                          title           VARCHAR(200) NOT NULL,
                          description     TEXT,
                          status          ENUM('ACTIVE', 'SOLD', 'FAILED', 'DELETED') DEFAULT 'ACTIVE', -- ACTIVE, SOLD, FAILED, DELETED
                          view_count      INT DEFAULT 0,
                          created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (seller_id) REFERENCES users(user_id),
                          FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- 3. 상품 이미지 (Product_Images)
CREATE TABLE product_images (
                                image_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                                product_id      BIGINT NOT NULL,
                                image_url       VARCHAR(500) NOT NULL,
                                image_order     INT DEFAULT 1, -- 대표 이미지는 1번
                                FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- 4. 경매 정보 (Auctions)
CREATE TABLE auctions (
                          auction_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                          product_id      BIGINT NOT NULL UNIQUE, -- 상품 하나당 경매 하나
                          start_price     BIGINT NOT NULL,
                          current_price   BIGINT NOT NULL, -- 입찰 들어올 때마다 갱신
                          start_time      DATETIME NOT NULL,
                          end_time        DATETIME NOT NULL,
                          status          VARCHAR(20) DEFAULT 'PROCEEDING', -- PROCEEDING, ENDED, CANCELED
                          FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- 5. 입찰 내역 (Bids) - 로그 성격
CREATE TABLE bids (
                      bid_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                      auction_id      BIGINT NOT NULL,
                      bidder_id       BIGINT NOT NULL, -- 입찰자
                      bid_price       BIGINT NOT NULL,
                      bid_time        DATETIME DEFAULT CURRENT_TIMESTAMP,
                      status          ENUM('ACTIVE', 'INVALID', 'CANCELED') DEFAULT 'ACTIVE',
                      FOREIGN KEY (auction_id) REFERENCES auctions(auction_id),
                      FOREIGN KEY (bidder_id) REFERENCES users(user_id)
);

-- 6. 주문/결제 (Orders) - 낙찰 후 최종 거래
CREATE TABLE orders (
                        order_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                        auction_id      BIGINT NOT NULL,
                        buyer_id        BIGINT NOT NULL,
                        final_price     BIGINT NOT NULL,
                        payment_status  VARCHAR(20) DEFAULT 'COMPLETED',
                        created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (auction_id) REFERENCES auctions(auction_id),
                        FOREIGN KEY (buyer_id) REFERENCES users(user_id)
);

-- 7. 판매자 평점 (Seller_Ratings)
CREATE TABLE seller_ratings (
                                rating_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                                to_user_id      BIGINT NOT NULL, -- 평가 받는 판매자
                                from_user_id    BIGINT NOT NULL, -- 평가 하는 구매자
                                order_id        BIGINT NOT NULL,
                                score           INT NOT NULL CHECK (score BETWEEN 1 AND 5),
                                comment         VARCHAR(100) NULL,
                                status          ENUM('NORMAL', 'DELETED') DEFAULT 'NORMAL',
                                created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (to_user_id) REFERENCES users(user_id),
                                FOREIGN KEY (from_user_id) REFERENCES users(user_id),
                                FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

SET FOREIGN_KEY_CHECKS = 1;