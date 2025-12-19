-- bulletin-board DB 스키마 DDL (최신본 / 실제 DB 기준)
-- MySQL 기준

CREATE DATABASE IF NOT EXISTS bulletin_board
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE bulletin_board;

-- =========================================================
-- 1) 회원 테이블 (members)
-- =========================================================
CREATE TABLE IF NOT EXISTS members (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)   NOT NULL,
    password VARCHAR(255)  NOT NULL,
    name     VARCHAR(50)   NOT NULL,
    email    VARCHAR(100),
    role     VARCHAR(20)   NOT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    password_changed_at DATETIME NULL,
    last_login_at       DATETIME NULL,
    login_fail_count    INT        NOT NULL DEFAULT 0,
    account_locked      TINYINT(1) NOT NULL DEFAULT 0,
    account_locked_at   DATETIME NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT uq_members_username UNIQUE (username),
    CONSTRAINT uq_members_email    UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================
-- 2) 게시글 테이블 (articles)
-- =========================================================
CREATE TABLE IF NOT EXISTS articles (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT       NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    view_count INT          NOT NULL DEFAULT 0,

    status                 VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    admin_memo             TEXT NULL,
    admin_memo_updated_at  DATETIME NULL,
    admin_memo_admin_id    BIGINT NULL, -- (현재는 제약 없는 단순 컬럼)

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_articles_member
        FOREIGN KEY (member_id)
        REFERENCES members(id),

    KEY idx_articles_member (member_id),
    KEY idx_articles_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================
-- 3) 댓글 테이블 (comments)
-- =========================================================
CREATE TABLE IF NOT EXISTS comments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT        NOT NULL,
    member_id  BIGINT        NOT NULL,
    content    VARCHAR(500)  NOT NULL,

    status                 VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    admin_memo             TEXT NULL,
    admin_memo_updated_at  DATETIME NULL,
    admin_memo_admin_id    BIGINT NULL, -- (현재는 제약 없는 단순 컬럼)

    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_article
        FOREIGN KEY (article_id)
        REFERENCES articles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comments_member
        FOREIGN KEY (member_id)
        REFERENCES members(id),

    KEY idx_comments_article (article_id, created_at),
    KEY idx_comments_member  (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================================================
-- 참고
-- =========================================================
-- 1) 회원 FK에는 ON DELETE CASCADE를 걸지 않았다.
--    → 회원탈퇴는 status 변경 방식, 작성 데이터는 유지
-- 2) 게시글 삭제 시 댓글은 ON DELETE CASCADE로 함께 삭제
-- 3) 이메일은 선택값이나, 입력 시 UNIQUE 보장
--    → 애플리케이션에서 공백/빈문자열은 NULL로 정규화
--
-- [추후 권장]
-- 관리자 메모 작성자 무결성 강화가 필요하면 FK 추가:
-- ALTER TABLE articles
--   ADD CONSTRAINT fk_articles_admin_memo_admin
--   FOREIGN KEY (admin_memo_admin_id)
--   REFERENCES members(id);
--
-- ALTER TABLE comments
--   ADD CONSTRAINT fk_comments_admin_memo_admin
--   FOREIGN KEY (admin_memo_admin_id)
--   REFERENCES members(id);
