package com.rejs.flashnote.domain.note.search;

import com.meilisearch.sdk.model.Task;
import com.meilisearch.sdk.model.TaskInfo;
import com.meilisearch.sdk.model.TaskStatus;
import com.rejs.flashnote.domain.note.search.document.NoteDocument;
import com.rejs.flashnote.domain.note.search.repository.NoteSyncRepository;
import com.rejs.flashnote.domain.sync.SyncRepository;
import com.rejs.flashnote.global.meilisearch.template.MeilisearchTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteSyncServiceTest {

    @Mock
    private NoteSyncRepository noteSyncRepository;
    @Mock private SyncRepository syncRepository;
    @Mock private MeilisearchTemplate meilisearchTemplate;

    @InjectMocks
    private NoteSyncService noteSyncService;

    @Test
    @DisplayName("동기화할 데이터가 없으면 Meilisearch를 호출하지 않고 종료한다")
    void sync_noData_stopEarly() {
        // given
        when(syncRepository.findLastUpdatedAtByEntityType(anyString()))
                .thenReturn(Optional.of(Instant.EPOCH));
        when(noteSyncRepository.findNoteDocumentsForSync(any(),any(), anyInt()))
                .thenReturn(List.of()); // 빈 리스트

        // when
        noteSyncService.sync();

        // then
        verify(meilisearchTemplate, never()).saveAll(any(), any());
        verify(syncRepository, never()).updateSyncTime(anyString(), any());
    }

    @Test
    @DisplayName("Meilisearch 작업이 성공하면 DB의 업데이트 시간을 갱신한다")
    void sync_success_updateMetadata() {
        // given
        Instant lastTime = Instant.now();
        NoteDocument doc = NoteDocument.builder().updatedAt(lastTime).build();

        when(syncRepository.findLastUpdatedAtByEntityType(any()))
                .thenReturn(Optional.of(Instant.EPOCH));
        when(noteSyncRepository.findNoteDocumentsForSync(any(), any(),anyInt()))
                .thenReturn(List.of(doc))
                .thenReturn(List.of());

        TaskInfo mockTaskInfo = new TaskInfo();
        Task task = new Task();
        Task spyTask = spy(task);
        doReturn(TaskStatus.SUCCEEDED).when(spyTask).getStatus();

        when(meilisearchTemplate.saveAll(any(), any())).thenReturn(mockTaskInfo);
        when(meilisearchTemplate.waitForTask(any(), any(),anyInt(),anyInt())).thenReturn(spyTask);

        // when
        noteSyncService.sync();

        // then
        verify(syncRepository).updateSyncTime(anyString(), eq(lastTime));
    }
}