CREATE TABLE members (
                         member_id    BIGINT       NOT NULL AUTO_INCREMENT,
                         email        VARCHAR(255) NOT NULL,
                         name         VARCHAR(100),
                         provider     VARCHAR(50),
                         role         VARCHAR(20)  DEFAULT 'ROLE_USER',
                         created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                         deleted_at   DATETIME(6)  NULL,
                         PRIMARY KEY (member_id),
                         CONSTRAINT uk_members_email_provider UNIQUE (email, provider)
);

CREATE INDEX idx_members_email ON members(email);
CREATE INDEX idx_members_deleted_at ON members(deleted_at);