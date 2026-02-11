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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

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

        // 전체 개수가 인식될 때까지 대기
        assertThat(result.getTotalElements()).isEqualTo(totalDocs);
        assertThat(result.getNumberOfElements()).isEqualTo(pageSize);
    }
}