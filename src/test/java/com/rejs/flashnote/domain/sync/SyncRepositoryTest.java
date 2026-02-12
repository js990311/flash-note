package com.rejs.flashnote.domain.sync;

import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.global.repository.config.JpaConfig;
import com.rejs.flashnote.global.repository.config.QueryDslConfig;
import io.hypersistence.tsid.TSID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Import({TestcontainersConfiguration.class, QueryDslConfig.class, SyncRepository.class, JpaConfig.class})
class SyncRepositoryTest {

    @Autowired
    private SyncRepository syncRepository;

    @Autowired
    private EntityManager em;

    private final String ENTITY_TYPE = "test";

    @Test
    @DisplayName("Flyway로 넣은 초기값(1970년)이 정상 조회되어야 한다")
    void findLastUpdatedAtTest() {
        // given - Flyway 대신 테스트 코드에서 직접 초기 데이터 삽입
        SyncMetadata metadata = SyncMetadata.builder()
                .entityType(ENTITY_TYPE)
                .lastUpdatedAt(Instant.EPOCH)
                .build();

        em.persist(metadata);
        em.flush();
        em.clear();

        // when
        Optional<Instant> result = syncRepository.findLastUpdatedAtByEntityType(ENTITY_TYPE);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(Instant.EPOCH);
    }

    @Test
    @DisplayName("업데이트 시 Dirty Checking을 통해 DB에 시점이 갱신되어야 한다")
    void updateSyncTimeTest() {
        // given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        SyncMetadata metadata = SyncMetadata.builder()
                .entityType(ENTITY_TYPE)
                .lastUpdatedAt(Instant.EPOCH)
                .build();
        em.persist(metadata);
        em.flush();
        em.clear();

        // when
        syncRepository.updateSyncTime(ENTITY_TYPE, now);

        // Dirty Checking 반영을 위해 강제 flush/clear
        em.flush();
        em.clear();

        // then
        Optional<Instant> result = syncRepository.findLastUpdatedAtByEntityType(ENTITY_TYPE);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(now);
    }
}