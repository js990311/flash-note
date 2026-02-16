package com.rejs.flashnote.domain.note.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.flashnote.domain.member.entity.QMember;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.NoteSummaryDto;
import com.rejs.flashnote.domain.note.entity.QNote;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class MyNoteGroupRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private QMember member = QMember.member;
    private QNote note = QNote.note;


    public Page<NoteDto> findByMemberId(Long memberId, Pageable pageable){
        List<NoteDto> content = jpaQueryFactory.select(
                        Projections.constructor(
                                NoteDto.class,
                                note.id,
                                note.title,
                                note.content,
                                note.updatedAt,
                                note.member.id,
                                note.published
                        )
                )
                .from(note)
                .where(note.member.id.eq(memberId))
                .orderBy(note.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        ;
        JPAQuery<Long> countq = jpaQueryFactory
                .select(note.count())
                .from(note)
                .where(note.member.id.eq(memberId))
        ;
        return PageableExecutionUtils.getPage(content, pageable, countq::fetchOne);

    }

    public List<NoteSummaryDto> findByMemberId(Long memberId, boolean isPublic){
        List<NoteSummaryDto> content = jpaQueryFactory.select(
                        Projections.constructor(
                                NoteSummaryDto.class,
                                note.id,
                                note.title,
                                note.member.id,
                                note.published,
                                note.createdAt,
                                note.updatedAt,
                                note.deletedAt
                        )
                )
                .from(note)
                .where(note.member.id.eq(memberId),
                        isPublic ? note.published.eq(true) : null
                )
                .orderBy(note.updatedAt.desc())
                .limit(15)
                .fetch();
        ;
        return content;
    }

}
