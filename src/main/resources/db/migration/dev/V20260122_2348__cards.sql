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
                       CONSTRAINT fk_decks_members FOREIGN KEY (member_id) REFERENCES members(member_id)
) ENGINE=InnoDB;

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
) ENGINE=InnoDB;