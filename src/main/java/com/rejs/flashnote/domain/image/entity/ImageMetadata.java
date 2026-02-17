package com.rejs.flashnote.domain.image.entity;

import com.rejs.flashnote.global.repository.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image_metadatas")
@Entity
@SQLDelete(sql = "UPDATE image_metadatas SET deleted_at = NOW() WHERE image_metadata_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
public class ImageMetadata extends BaseEntity {
    @Tsid
    @Id
    @Column(name = "image_metadata_id")
    private Long id;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    private Long fileSize;

    private Long memberId;
}
