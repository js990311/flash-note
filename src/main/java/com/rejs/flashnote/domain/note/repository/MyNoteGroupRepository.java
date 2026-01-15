package com.rejs.flashnote.domain.note.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.flashnote.domain.member.entity.QMember;
import com.rejs.flashnote.domain.note.dto.NoteGroupListDto;
import com.rejs.flashnote.domain.note.entity.QNote;
import com.rejs.flashnote.domain.note.entity.QNoteGroup;
import com.rejs.flashnote.domain.note.entity.QNotePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MyNoteGroupRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private QMember member = QMember.member;
    private QNoteGroup noteGroup = QNoteGroup.noteGroup;
    private QNotePermission notePermission = QNotePermission.notePermission;
    private QNote note = QNote.note;

    public Page<NoteGroupListDto> findByMyPage(Long memberId, Pageable pageable) {
        List<NoteGroupListDto> content = jpaQueryFactory
                .select(Projections.constructor(
                        NoteGroupListDto.class,
                        noteGroup.id,
                        noteGroup.name,
                        notePermission.role.stringValue(),
                        noteGroup.updatedAt
                ))
                .from(notePermission)
                .join(notePermission.noteGroup, noteGroup)
                .where(notePermission.member.id.eq(memberId))
                .orderBy(noteGroup.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countq = jpaQueryFactory
                .select(notePermission.count())
                .from(notePermission)
                .where(notePermission.member.id.eq(memberId));
        return PageableExecutionUtils.getPage(content, pageable, countq::fetchOne);
    }
}
