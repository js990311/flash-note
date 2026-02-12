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

    public void sync(){
        try {
            Optional<Instant> opt = syncRepository.findLastUpdatedAtByEntityType(entityType);
            Instant lastUpdateTime = opt.orElseThrow(()->new MeilisearchSyncException(entityType + "에 대한 sync metadata가 없음"));

            List<NoteDocument> noteDocumentsForSync = noteSyncRepository.findNoteDocumentsForSync(lastUpdateTime, 1000);

            if(noteDocumentsForSync.isEmpty()){
                return;
            }

            TaskInfo taskInfo = meilisearchTemplate.saveAll(NoteDocument.class, noteDocumentsForSync);
            Task task = meilisearchTemplate.waitForTask(NoteDocument.class, taskInfo);

            if(task.getStatus().equals(TaskStatus.SUCCEEDED)){
                Instant lastProcessedUpdatedAt = noteDocumentsForSync.getLast().getUpdatedAt();
                syncRepository.updateSyncTime(entityType, lastProcessedUpdatedAt);
                log.info("Meilisearch sync 성공: {} 건, 시점: {}", noteDocumentsForSync.size(), lastProcessedUpdatedAt);
            } else {
                log.error("Meilisearch sync 실패: {}", task.getError());
                throw new MeilisearchSyncException(task.getError().getMessage());
            }
        }catch (Exception e){
            throw new MeilisearchSyncException(e);
        }
    }
}
