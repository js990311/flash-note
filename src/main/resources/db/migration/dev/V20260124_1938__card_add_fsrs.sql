ALTER TABLE cards
    ADD COLUMN last_review_at DATETIME(6) NULL,
    ADD COLUMN state VARCHAR(20) NULL DEFAULT 'LEARNING',
    ADD COLUMN fsrs_json TEXT NULL,
    ADD COLUMN due DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6);

-- 인덱스 추가 (조회 성능 최적화)
CREATE INDEX idx_cards_due ON cards (due);
