package com.rejs.flashnote.domain.note.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.flashnote.domain.member.entity.QMember;
import com.rejs.flashnote.domain.note.entity.QNote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class NoteAuthorizeRepository {
    private final JPAQueryFactory queryFactory;
    private QMember member = QMember.member;
    private QNote note = QNote.note;

    public boolean authorize(Long memberId, Long entityId, boolean isOwnerOnly){
        Integer fetchOne = queryFactory
                    .selectOne()
                    .from(note)
                    .where(
                        note.id.eq(entityId),
                        note.deletedAt.isNull(),
                        accessCondition(memberId, isOwnerOnly)
                    )
                    .fetchFirst();
        return fetchOne != null;
    }

    private BooleanExpression accessCondition(Long memberId, boolean isOwnerOnly) {
        if (isOwnerOnly) {
            return note.member.id.eq(memberId);
        }
        return note.member.id.eq(memberId).or(note.published.isTrue());
    }
}
