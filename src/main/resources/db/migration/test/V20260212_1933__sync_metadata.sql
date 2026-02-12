CREATE TABLE sync_metadatas (
    sync_metadata_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type      VARCHAR(50) NOT NULL UNIQUE,
    last_updated_at  DATETIME(6)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at   DATETIME(6)  NULL
) ENGINE=InnoDB;

INSERT INTO sync_metadatas (entity_type, last_updated_at)
VALUES('notes', '1970-01-01 00:00:00.000000')
