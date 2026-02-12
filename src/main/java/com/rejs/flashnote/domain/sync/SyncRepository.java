package com.rejs.flashnote.domain.sync;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SyncRepository {
    private final JPAQueryFactory queryFactory;

    private QSyncMetadata syncMetadata = QSyncMetadata.syncMetadata;

    @Transactional(readOnly = true)
    public Optional<Instant> findLastUpdatedAtByEntityType(String entityType) {
        return Optional.ofNullable(
                queryFactory
                        .select(syncMetadata.lastUpdatedAt)
                        .from(syncMetadata)
                        .where(syncMetadata.entityType.eq(entityType))
                        .fetchOne()
        );
    }

    @Transactional
    public void updateSyncTime(String entityType, Instant lastProcessedTime) {
        SyncMetadata entity = queryFactory
                .selectFrom(syncMetadata)
                .where(syncMetadata.entityType.eq(entityType))
                .fetchOne();
        entity.update(lastProcessedTime);
    }
}
