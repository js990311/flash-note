package com.rejs.flashnote.domain.note.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoteSyncScheduler {
    private final NoteSyncService noteSyncService;

    @Scheduled(fixedDelay = 60000)
    public void scheduleNoteSync() {
        log.info("[note sync] scheduler");
        try {
            noteSyncService.sync();
        } catch (Exception e) {
            log.error("[note.sync.fail] 동기화 실패", e);
        }
    }
}
