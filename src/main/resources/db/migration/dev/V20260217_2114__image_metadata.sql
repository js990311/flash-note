CREATE TABLE `image_metadatas` (
   `image_metadata_id` BIGINT NOT NULL,
   `s3_key`            VARCHAR(512) NOT NULL,
   `file_name`         VARCHAR(255) NOT NULL,
   `content_type`      VARCHAR(100) NOT NULL,
   `file_size`         BIGINT,
   `is_uploaded`       TINYINT(1) NOT NULL DEFAULT 0,
   `member_id`         BIGINT,
   created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
   updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
   deleted_at   DATETIME(6)  NULL,
   PRIMARY KEY (`image_metadata_id`),
   CONSTRAINT fk_image_members FOREIGN KEY (member_id) REFERENCES members(member_id)
) ;