CREATE TABLE note_groups (
                             note_group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                             updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                             deleted_at   DATETIME(6)  NULL
) ENGINE=InnoDB;

CREATE TABLE note_permissions (
                                  note_permission_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  role VARCHAR(50) NOT NULL,
                                  note_group_id BIGINT NOT NULL,
                                  member_id BIGINT NOT NULL,
                                  created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                  updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                                  deleted_at   DATETIME(6)  NULL,
                                  CONSTRAINT fk_permission_note_group FOREIGN KEY (note_group_id) REFERENCES note_groups(note_group_id),
                                  CONSTRAINT fk_permission_member FOREIGN KEY (member_id) REFERENCES members(member_id)
) ENGINE=InnoDB;

CREATE TABLE notes (
                       note_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       content MEDIUMTEXT,
                       note_group_id BIGINT,
                       created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       deleted_at   DATETIME(6)  NULL,
                       CONSTRAINT fk_note_note_group FOREIGN KEY (note_group_id) REFERENCES note_groups(note_group_id)
) ENGINE=InnoDB;