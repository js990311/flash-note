package com.rejs.flashnote.domain.note.search.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.flashnote.domain.note.entity.QNote;
import com.rejs.flashnote.domain.note.search.document.NoteDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class NoteSyncRepository {
    private final JPAQueryFactory queryFactory;
    private QNote note = QNote.note;
    public List<NoteDocument> findNoteDocumentsForSync(Instant lastSyncTime, int limit) {
        return queryFactory
                .select(Projections.constructor(NoteDocument.class,
                        note.id,
                        note.title,
                        note.content,
                        note.member.id, // join 없이 id만 바로 참조 가능
                        note.published,
                        note.createdAt,
                        note.updatedAt,
                        note.deletedAt
                ))
                .from(note)
                .where(note.updatedAt.gt(lastSyncTime))
                .orderBy(note.updatedAt.asc())
                .limit(limit)
                .fetch();
    }
}
