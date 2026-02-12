package com.rejs.flashnote.domain.sync;

import com.rejs.flashnote.global.repository.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@SQLDelete(sql = "UPDATE sync_metadatas SET deleted_at = NOW() WHERE sync_metadata_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "sync_metadatas")
public class SyncMetadata extends BaseEntity {
    @Id
    @Tsid
    @Column(name = "sync_metadata_id")
    private Long id;

    @Column
    private String entityType;

    @Column
    private Instant lastUpdatedAt;
}
