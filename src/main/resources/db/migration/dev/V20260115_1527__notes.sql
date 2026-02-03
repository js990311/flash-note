CREATE TABLE notes (
                       note_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                        member_id BIGINT NOT NULL,
                       content MEDIUMTEXT,
                       created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at   DATETIME(6)  NULL,
                       CONSTRAINT fk_note_member FOREIGN KEY (member_id) REFERENCES members(member_id)
) ENGINE=InnoDB;