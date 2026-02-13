package com.rejs.flashnote.domain.note.search;

import com.meilisearch.sdk.model.SearchResult;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.repository.NoteRepository;
import com.rejs.flashnote.domain.note.search.document.NoteDocument;
import com.rejs.flashnote.domain.sync.SyncRepository;
import com.rejs.flashnote.global.meilisearch.document.DocumentMetadatas;
import com.rejs.flashnote.global.meilisearch.template.MeilisearchQuery;
import com.rejs.flashnote.global.meilisearch.template.MeilisearchTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class NoteSyncServiceIntegrationTest {

    @Autowired private NoteSyncService noteSyncService;
    @Autowired private SyncRepository syncRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private MeilisearchTemplate meilisearchTemplate;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("전체 동기화 흐름 테스트: DB 변경 데이터가 Meilisearch에 반영되고 메타데이터가 갱신된다")
    void sync_integration_test() {
        // [Given] 1. 회원 및 노트를 DB에 저장
        Member member = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));

        // 검색에서 찾기 쉬운 유니크한 제목 생성
        String uniqueTitle = "Sync-Test-" + java.util.UUID.randomUUID();
        Note note = fixtureMonkey.giveMeBuilder(Note.class)
                .set(javaGetter(Note::getMember), member)
                .set(javaGetter(Note::getTitle), uniqueTitle)
                .set(javaGetter(Note::getDeletedAt), null) // 삭제되지 않은 상태
                .sample();

        noteRepository.save(note);
        // DB 정밀도 이슈를 고려하여 생성된 시점을 획득
        Instant noteUpdatedAt = note.getUpdatedAt().truncatedTo(ChronoUnit.MICROS);

        // [When] 동기화 실행
        noteSyncService.sync();

        // [Then] 1. Meilisearch에 데이터가 들어갔는지 확인 (Template을 통한 직접 검색)
        // waitForTask는 service.sync() 내부에서 수행되므로 즉시 조회 가능
        Slice<NoteDocument> search = meilisearchTemplate.searchSlice(NoteDocument.class, MeilisearchQuery.builder().query(uniqueTitle).pageable(PageRequest.of(0, 10)).build());
        assertThat(search.getContent()).hasSize(1);
        assertThat(search.getContent().getFirst().getTitle()).isEqualTo(uniqueTitle);

        // [Then] 2. SyncMetadata가 마지막 노트의 수정 시간으로 갱신되었는지 확인
        Optional<Instant> lastSyncTime = syncRepository.findLastUpdatedAtByEntityType(DocumentMetadatas.NOTE.getIndexName());
        assertThat(lastSyncTime).isPresent();

        // DB 저장 시의 미세 오차를 고려하여 비교 (isAfterOrEqualTo 또는 truncated 비교)
        assertThat(lastSyncTime.get()).isAfterOrEqualTo(noteUpdatedAt);
    }

    @Test
    @DisplayName("증분 색인 테스트: 이미 동기화된 데이터 이후의 새 데이터만 추가로 동기화한다")
    void incremental_sync_test() {
        // [Given] 1. 첫 번째 노트 저장 및 동기화 완료
        Member member = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        noteRepository.save(fixtureMonkey.giveMeBuilder(Note.class).set(javaGetter(Note::getMember), member).sample());
        noteSyncService.sync();

        Instant firstSyncTime = syncRepository.findLastUpdatedAtByEntityType(DocumentMetadatas.NOTE.getIndexName()).orElseThrow();

        // [Given] 2. 두 번째 새 노트 저장 (수정 시간이 첫 번째보다 뒤임이 보장됨)
        String secondTitle = "Second-Note-" + java.util.UUID.randomUUID();
        Note secondNote = fixtureMonkey.giveMeBuilder(Note.class)
                .set(javaGetter(Note::getMember), member)
                .set(javaGetter(Note::getTitle), secondTitle)
                .sample();
        noteRepository.save(secondNote);
        Instant secondNoteTime = secondNote.getUpdatedAt().truncatedTo(ChronoUnit.MICROS);

        // [When] 다시 동기화 실행
        noteSyncService.sync();

        // [Then] 메타데이터가 두 번째 노트의 시간으로 업데이트되었는지 확인
        Instant secondSyncTime = syncRepository.findLastUpdatedAtByEntityType(DocumentMetadatas.NOTE.getIndexName()).orElseThrow();
        assertThat(secondSyncTime).isAfter(firstSyncTime);
        assertThat(secondSyncTime).isAfterOrEqualTo(secondNoteTime);

        // Meilisearch에서 두 번째 노트 검색 확인
        Slice<NoteDocument> search = meilisearchTemplate.searchSlice(NoteDocument.class, MeilisearchQuery.builder().query(secondTitle).pageable(PageRequest.of(0, 10)).build());
        assertThat(search.getContent()).hasSize(1);
    }
}