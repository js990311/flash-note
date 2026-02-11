package com.rejs.flashnote.global.meilisearch.template;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.Task;
import com.meilisearch.sdk.model.TaskInfo;
import com.meilisearch.sdk.model.TaskStatus;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.note.search.NoteDocument;
import com.rejs.flashnote.global.meilisearch.document.DocumentMetadatas;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "spring.jackson.serialization.write-dates-as-timestamps=false")
class MeilisearchTemplateTest {
    private FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Autowired
    private MeilisearchTemplate meilisearchTemplate;

    @Autowired
    private Client client;

    String targetKeyword = "Target-" + UUID.randomUUID();
    String noiseKeyword = "Noise-" + UUID.randomUUID();


    @Test
    void save() {
        NoteDocument noteDocument = fixtureMonkey.giveMeOne(NoteDocument.class);
        TaskInfo taskInfo = meilisearchTemplate.save(NoteDocument.class, noteDocument);
        assertThat(taskInfo).isNotNull();
        Task task = meilisearchTemplate.getTask(NoteDocument.class, taskInfo);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.SUCCEEDED);
    }

    @Test
    void saveAll() {
        List<NoteDocument> noteDocuments = fixtureMonkey.giveMe(NoteDocument.class,5);
        TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, noteDocuments);
        Task task = meilisearchTemplate.getTask(NoteDocument.class, taskInfo);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.SUCCEEDED);
    }

    @Test
    @DisplayName("문서를 저장하면 비동기 인덱싱 후 검색되어야 한다")
    void saveAndSearch() {
        // Given
        String uniqueTitle = "TestTitle-" + UUID.randomUUID();

        NoteDocument noteDocument = fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set("title", uniqueTitle)
                .sample();

        // When
        TaskInfo taskInfo = meilisearchTemplate.save(NoteDocument.class, noteDocument);
        Task task = meilisearchTemplate.getTask(NoteDocument.class, taskInfo);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.SUCCEEDED);

        SearchRequest request = new SearchRequest(uniqueTitle);
        Pageable pageable = PageRequest.of(0, 10);

        Page<NoteDocument> result = meilisearchTemplate.search(NoteDocument.class, request, pageable);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(uniqueTitle);
    }

    @Test
    @DisplayName("페이징 요청 시 지정된 사이즈만큼 문서가 반환되어야 한다")
    void searchPagination() {
        // Given
        String commonKeyword = "CommonKeyword-" + UUID.randomUUID();
        int totalDocs = 15;
        int pageSize = 5;

        // ID 포맷 강제 및 리스트 생성
        List<NoteDocument> documents = fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set("title", commonKeyword)
                .sampleList(totalDocs);

        TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, documents);
        Task task = meilisearchTemplate.getTask(NoteDocument.class, taskInfo);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.SUCCEEDED);

        SearchRequest request = new SearchRequest(commonKeyword);
        Pageable pageable = PageRequest.of(0, pageSize);

        Page<NoteDocument> result = meilisearchTemplate.search(NoteDocument.class, request, pageable);

        assertThat(result.getTotalElements()).isEqualTo(totalDocs);
        assertThat(result.getNumberOfElements()).isEqualTo(pageSize);
    }

    @Test
    @DisplayName("페이징 요청 시 지정된 사이즈만큼 문서가 반환되어야 한다(Dsl)")
    void searchPaginationByDsl() {
        // Given
        String commonKeyword = "Pagination-" + UUID.randomUUID();
        int totalDocs = 15;
        int pageSize = 5;

        // ID 포맷 강제 및 리스트 생성
        List<NoteDocument> documents = fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set("title", commonKeyword)
                .sampleList(totalDocs);

        TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, documents);
        Task task = meilisearchTemplate.getTask(NoteDocument.class, taskInfo);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.SUCCEEDED);

        Pageable pageable = PageRequest.of(0, pageSize);
        MeilisearchQuery request = MeilisearchQuery.builder()
                .query(commonKeyword)
                .pageable(pageable)
                .build();

        Page<NoteDocument> result = meilisearchTemplate.search(NoteDocument.class, request);

        assertThat(result.getTotalElements()).isEqualTo(totalDocs);
        assertThat(result.getNumberOfElements()).isEqualTo(pageSize);
    }

    @BeforeEach
    void setUp() {
        // 1. 테스트마다 고유한 키워드 생성 (데이터 격리)
        List<NoteDocument> documents = new ArrayList<>();

        // [Doc 1] Title에만 키워드 (ID: 1)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set("noteId", 1L)
                .set("title", targetKeyword)
                .set("content", noiseKeyword)
                .sample());

        // [Doc 2] Content에만 키워드 (ID: 2)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set("noteId", 2L)
                .set("title", noiseKeyword)
                .set("content", targetKeyword)
                .sample());

        // [Doc 3] 둘 다 없음 (ID: 3)
        documents.add(fixtureMonkey.giveMeBuilder(NoteDocument.class)
                .set("noteId", 3L)
                .set("title", noiseKeyword)
                .set("content", noiseKeyword)
                .sample());

        // 2. 저장 및 인덱싱 대기 (공통 준비 과정)
        TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, documents);
        client.index("notes").waitForTask(taskInfo.getTaskUid());
    }

    @Test
    @DisplayName("기본 검색: 필드를 지정하지 않으면 Title과 Content 모두에서 검색되어야 한다")
    void searchDefault_ShouldSearchAllFields() {
        // Given
        MeilisearchQuery query = MeilisearchQuery.builder()
                .query(targetKeyword)
                // searchAttributes 없음 -> 전체 검색
                .pageable(PageRequest.of(0, 10))
                .build();

        // When
        Page<NoteDocument> result = meilisearchTemplate.search(NoteDocument.class, query);

        // Then: Doc1(Title), Doc2(Content) 둘 다 검색됨
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting("noteId")
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("필드 지정: Title만 지정하면 Content에 있는 문서는 검색되지 않아야 한다")
    void searchTitleOnly_ShouldIgnoreContentMatches() {
        // Given
        MeilisearchQuery query = MeilisearchQuery.builder()
                .query(targetKeyword)
                .searchAttributes(List.of("title")) // ★ Title만 지정
                .pageable(PageRequest.of(0, 10))
                .build();

        // When
        Page<NoteDocument> result = meilisearchTemplate.search(NoteDocument.class, query);

        // Then: Doc1(Title O)만 나와야 함
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNoteId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("필드 지정: Content만 지정하면 Title에 있는 문서는 검색되지 않아야 한다")
    void searchContentOnly_ShouldIgnoreTitleMatches() {
        // Given
        MeilisearchQuery query = MeilisearchQuery.builder()
                .query(targetKeyword)
                .searchAttributes(List.of("content")) // ★ Content만 지정
                .pageable(PageRequest.of(0, 10))
                .build();

        // When
        Page<NoteDocument> result = meilisearchTemplate.search(NoteDocument.class, query);

        // Then: Doc2(Content O)만 나와야 함
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNoteId()).isEqualTo(2L);
    }
}