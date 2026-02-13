package com.rejs.flashnote.domain.note.search.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.model.TaskInfo;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.NoteSummaryDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteSearchOption;
import com.rejs.flashnote.domain.note.search.document.NoteDocument;
import com.rejs.flashnote.global.meilisearch.template.MeilisearchTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class NoteSearchMeilisearchRepositoryTest {
    private FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Autowired
    private MeilisearchTemplate meilisearchTemplate;

    @Autowired
    private NoteSearchMeilisearchRepository noteSearchRepository;

    @Autowired
    private ObjectMapper objectMapper;

    String targetKeyword = "Target-" + UUID.randomUUID();
    String noiseKeyword = "Noise-" + UUID.randomUUID();
    Long myMemberId = 1L;
    Long otherMemberId = 999L;

    @BeforeEach
    void setUp()  {
        targetKeyword = "Target-" + UUID.randomUUID();
        noiseKeyword = "Noise-" + UUID.randomUUID();

        List<NoteDocument> documents = new ArrayList<>();

        // [Doc 1] 내 노트 & 공개 & Title에 키워드 (ID: 1)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 1L)
                .set(javaGetter(NoteDocument::getMemberId), myMemberId) // 내 노트
                .set(javaGetter(NoteDocument::isPublished), true)      // 공개
                .set(javaGetter(NoteDocument::getTitle), targetKeyword)
                .set(javaGetter(NoteDocument::getContent), noiseKeyword)
                .sample());

        // [Doc 2] 남의 노트 & 비공개 & Content에 키워드 (ID: 2)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 2L)
                .set(javaGetter(NoteDocument::getMemberId), otherMemberId) // 남의 노트
                .set(javaGetter(NoteDocument::isPublished), false)        // 비공개
                .set(javaGetter(NoteDocument::getTitle), noiseKeyword)
                .set(javaGetter(NoteDocument::getContent), targetKeyword)
                .sample());

        // [Doc 3] 내 노트 & 공개 & 키워드 없음 (ID: 3)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 3L)
                .set(javaGetter(NoteDocument::getMemberId), myMemberId)
                .set(javaGetter(NoteDocument::isPublished), true)
                .set(javaGetter(NoteDocument::getTitle), noiseKeyword)
                .set(javaGetter(NoteDocument::getContent), noiseKeyword)
                .sample());
        String json = null;
        try {
            json = objectMapper.writeValueAsString(documents.get(0));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("==========================================");
        System.out.println("🔥 JSON 변환 결과: " + json);
        System.out.println("==========================================");
        // 2. 저장 및 인덱싱 대기
        TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, documents);
        // Task 완료 대기 (필수)
        meilisearchTemplate.waitForTask(NoteDocument.class, taskInfo);
    }

    @Test
    @DisplayName("내 노트 검색: 내 memberId와 일치하고 키워드가 포함된 노트만 조회된다")
    void searchMyNote() {
        String targetKeyword = "MyNote-" + UUID.randomUUID();

        List<NoteDocument> documents = new ArrayList<>();

        // [Doc 1] 내 노트 & 공개 & Title에 키워드 (ID: 1)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 1L)
                .set(javaGetter(NoteDocument::getMemberId), myMemberId) // 내 노트
                .set(javaGetter(NoteDocument::isPublished), true)      // 공개
                .set(javaGetter(NoteDocument::getTitle), targetKeyword)
                .set(javaGetter(NoteDocument::getContent), noiseKeyword)
                .sample());

        // [Doc 2] 남의 노트 & 비공개 & Content에 키워드 (ID: 2)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 2L)
                .set(javaGetter(NoteDocument::getMemberId), otherMemberId) // 남의 노트
                .set(javaGetter(NoteDocument::isPublished), false)        // 비공개
                .set(javaGetter(NoteDocument::getTitle), noiseKeyword)
                .set(javaGetter(NoteDocument::getContent), targetKeyword)
                .sample());

        // [Doc 3] 내 노트 & 공개 & 키워드 없음 (ID: 3)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 3L)
                .set(javaGetter(NoteDocument::getMemberId), myMemberId)
                .set(javaGetter(NoteDocument::isPublished), true)
                .set(javaGetter(NoteDocument::getTitle), noiseKeyword)
                .set(javaGetter(NoteDocument::getContent), noiseKeyword)
                .sample());

        // 2. 저장 및 인덱싱 대기
        TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, documents);
        // Task 완료 대기 (필수)
        meilisearchTemplate.waitForTask(NoteDocument.class, taskInfo);


        // When
        Slice<NoteSummaryDto> result = noteSearchRepository.searchMyNote(
                myMemberId,
                targetKeyword,
                NoteSearchOption.TITLE_CONTENT,
                PageRequest.of(0, 10)
        );

        // Then
        // Doc 1 (내꺼 O, 키워드 O) -> 조회됨
        // Doc 2 (내꺼 X, 키워드 O) -> 조회 안됨
        // Doc 3 (내꺼 O, 키워드 X) -> 조회 안됨
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNoteId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("공개 노트 검색: published가 true이고 키워드가 포함된 노트만 조회된다")
    void searchPublicNote() {
        String targetKeyword = "Public-" + UUID.randomUUID();

        List<NoteDocument> documents = new ArrayList<>();

        // [Doc 1] 내 노트 & 공개 & Title에 키워드 (ID: 1)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 1L)
                .set(javaGetter(NoteDocument::getMemberId), myMemberId) // 내 노트
                .set(javaGetter(NoteDocument::isPublished), true)      // 공개
                .set(javaGetter(NoteDocument::getTitle), targetKeyword)
                .set(javaGetter(NoteDocument::getContent), noiseKeyword)
                .sample());

        // [Doc 2] 남의 노트 & 비공개 & Content에 키워드 (ID: 2)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 2L)
                .set(javaGetter(NoteDocument::getMemberId), otherMemberId) // 남의 노트
                .set(javaGetter(NoteDocument::isPublished), false)        // 비공개
                .set(javaGetter(NoteDocument::getTitle), noiseKeyword)
                .set(javaGetter(NoteDocument::getContent), targetKeyword)
                .sample());

        // [Doc 3] 내 노트 & 공개 & 키워드 없음 (ID: 3)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set(javaGetter(NoteDocument::getNoteId), 3L)
                .set(javaGetter(NoteDocument::getMemberId), myMemberId)
                .set(javaGetter(NoteDocument::isPublished), true)
                .set(javaGetter(NoteDocument::getTitle), noiseKeyword)
                .set(javaGetter(NoteDocument::getContent), noiseKeyword)
                .sample());

        // 2. 저장 및 인덱싱 대기
        TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, documents);
        // Task 완료 대기 (필수)
        meilisearchTemplate.waitForTask(NoteDocument.class, taskInfo);

        // When
        Slice<NoteSummaryDto> result = noteSearchRepository.searchPublicNote(
                targetKeyword,
                NoteSearchOption.TITLE_CONTENT,
                PageRequest.of(0, 10)
        );

        // Then
        // Doc 1 (공개 O, 키워드 O) -> 조회됨
        // Doc 2 (공개 X, 키워드 O) -> 조회 안됨 (비공개 필터링)
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNoteId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("검색 옵션(Title Only): 제목에 키워드가 있는 문서만 조회된다")
    void searchOption_TitleOnly() {
        // When: 옵션을 TITLE_ONLY로 설정하고 검색 (대상은 내 노트로 가정)
        Slice<NoteSummaryDto> result = noteSearchRepository.searchMyNote(
                myMemberId,
                targetKeyword,
                NoteSearchOption.TITLE, // ★ 제목만 검색
                PageRequest.of(0, 10)
        );

        // Then
        // Doc 1: Title에 targetKeyword 있음 -> 조회됨
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNoteId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("검색 옵션(Content Only): 내용에 키워드가 있는 문서만 조회된다")
    void searchOption_ContentOnly() {
        // Given: 테스트를 위해 Doc 2의 조건을 변경해서 검색해봄 (남의 노트지만 공개 검색으로 테스트)

        // When: 공개 노트 검색 + Content Only 옵션
        Slice<NoteSummaryDto> result = noteSearchRepository.searchPublicNote(
                targetKeyword,
                NoteSearchOption.CONTENT, // ★ 내용만 검색
                PageRequest.of(0, 10)
        );

        // Then
        // Doc 1: Content에는 noiseKeyword만 있음 -> 조회 안됨
        // Doc 2: Content에 targetKeyword가 있지만 비공개(published=false) -> 조회 안됨
        // 결과적으로 0건이어야 함 (필터링과 검색 옵션이 모두 동작함을 검증)
        assertThat(result.getNumberOfElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("검색 옵션(Content Only) 2: 내용에 있고 공개된 노트 찾기")
    void searchOption_ContentOnly_Positive() {
        // Given: Doc 4 추가 (공개 + 내용에 키워드)
        NoteDocument contentDoc = fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set("noteId", 4L)
                .set("published", true)
                .set("title", noiseKeyword)
                .set("content", targetKeyword)
                .sample();
        TaskInfo t = meilisearchTemplate.save(NoteDocument.class, contentDoc);
        meilisearchTemplate.waitForTask(NoteDocument.class, t);

        // When
        Slice<NoteSummaryDto> result = noteSearchRepository.searchPublicNote(
                targetKeyword,
                NoteSearchOption.CONTENT,
                PageRequest.of(0, 10)
        );

        // Then
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNoteId()).isEqualTo(4L);
    }

}