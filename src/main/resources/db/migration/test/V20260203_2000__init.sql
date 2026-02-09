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

CREATE TABLE notes (
                       note_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                        member_id BIGINT NOT NULL,
                       content MEDIUMTEXT,
                       created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at   DATETIME(6)  NULL,
                       CONSTRAINT fk_note_member FOREIGN KEY (member_id) REFERENCES members(member_id)
);

CREATE TABLE decks (
                       deck_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       member_id BIGINT NOT NULL,
                       original_type VARCHAR(255) NOT NULL DEFAULT "ORIGINAL",
                       original_id BIGINT NULL,
                       created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at   DATETIME(6)  NULL,
                       card_counts INTEGER NOT NULL DEFAULT 0,
                       state VARCHAR(20) NULL DEFAULT 'COMPLETED',
                       CONSTRAINT fk_decks_members FOREIGN KEY (member_id) REFERENCES members(member_id)
);

CREATE TABLE cards (
                       card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       front TEXT NOT NULL,
                       back TEXT NOT NULL,
                       member_id BIGINT NOT NULL,
                       deck_id BIGINT NOT NULL,
                       created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at   DATETIME(6)  NULL,
                       CONSTRAINT fk_cards_decks FOREIGN KEY (deck_id) REFERENCES decks(deck_id),
                       CONSTRAINT fk_cards_members FOREIGN KEY (member_id) REFERENCES members(member_id)
);

ALTER TABLE cards
    ADD COLUMN last_review_at DATETIME(6) NULL,
    ADD COLUMN state VARCHAR(20) NULL DEFAULT 'LEARNING',
    ADD COLUMN fsrs_json TEXT NULL,
    ADD COLUMN due DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6);

CREATE INDEX idx_cards_due ON cards (due);
