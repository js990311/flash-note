package com.rejs.flashnote.domain.note.search;

import com.meilisearch.sdk.model.Task;
import com.meilisearch.sdk.model.TaskInfo;
import com.meilisearch.sdk.model.TaskStatus;
import com.rejs.flashnote.domain.note.search.document.NoteDocument;
import com.rejs.flashnote.domain.note.search.repository.NoteSyncRepository;
import com.rejs.flashnote.domain.sync.SyncRepository;
import com.rejs.flashnote.global.meilisearch.document.DocumentMetadatas;
import com.rejs.flashnote.global.meilisearch.exception.MeilisearchException;
import com.rejs.flashnote.global.meilisearch.exception.MeilisearchSyncException;
import com.rejs.flashnote.global.meilisearch.template.MeilisearchTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoteSyncService {
    private final NoteSyncRepository noteSyncRepository;
    private final SyncRepository syncRepository;
    private final MeilisearchTemplate meilisearchTemplate;
    private final String entityType = DocumentMetadatas.NOTE.getIndexName();
    private static final int BATCH_SIZE = 128;

    public void sync(){
        try {
            Instant syncLimitTime = Instant.now().truncatedTo(ChronoUnit.MICROS);
            Optional<Instant> opt = syncRepository.findLastUpdatedAtByEntityType(entityType);
            Instant lastUpdateTime = opt.orElseThrow(()->new MeilisearchSyncException(entityType + "에 대한 sync metadata가 없음"));

            log.info("[note.sync.start] 기준 시점: {}", lastUpdateTime);
            while (true){
                List<NoteDocument> noteDocumentsForSync = noteSyncRepository.findNoteDocumentsForSync(lastUpdateTime, syncLimitTime,BATCH_SIZE);

                if(noteDocumentsForSync.isEmpty()){
                    log.info("[note.sync.end] 더 이상 동기화할 데이터가 없습니다.");
                    break;
                }

                TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, noteDocumentsForSync);
                log.info("[note.sync] task Id : {}", taskInfo.getTaskUid());
                Task task = meilisearchTemplate.waitForTask(NoteDocument.class, taskInfo, 90000,1000);

                if(task.getStatus().equals(TaskStatus.SUCCEEDED)){
                    lastUpdateTime = noteDocumentsForSync.getLast().getUpdatedAt();
                    syncRepository.updateSyncTime(entityType, lastUpdateTime);
                    log.info("[note.sync.success] 성공: {} 건, 시점: {}", noteDocumentsForSync.size(), lastUpdateTime);
                } else {
                    log.error("[note.sync.fail] 실패: {}", task.getError());
                    throw new MeilisearchSyncException(task.getError().getMessage());
                }
            }
        }catch (Exception e){
            throw new MeilisearchSyncException(e);
        }
    }
}
