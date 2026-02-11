package com.rejs.flashnote.domain.note.search.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteSearchOption;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.entity.QNote;
import com.rejs.flashnote.domain.note.search.repository.NoteSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
//@Repository
public class NoteSearchQueryDslRepository implements NoteSearchRepository {
    private final JPAQueryFactory queryFactory;
    private QNote note = QNote.note;

    public Page<NoteDto> searchMyNote(Long memberId, String keyword, NoteSearchOption searchOption, Pageable pageable){
        List<Note> content = queryFactory
                .selectFrom(note)
                .where(
                        note.member.id.eq(memberId),
                        note.deletedAt.isNull(),
                        keywordCondition(keyword, searchOption)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(note.count())
                .from(note)
                .where(
                        note.member.id.eq(memberId),
                        note.deletedAt.isNull(),
                        keywordCondition(keyword, searchOption)
                );

        return PageableExecutionUtils.getPage(content.stream().map(NoteDto::from).toList(), pageable, countQuery::fetchOne);
    }

    @Override
    public Page<NoteDto> searchPublicNote(String keyword, NoteSearchOption searchOption, Pageable pageable) {
        List<Note> content = queryFactory
                .selectFrom(note)
                .where(
                        note.published.isTrue(),
                        note.deletedAt.isNull(),
                        keywordCondition(keyword, searchOption)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(note.count())
                .from(note)
                .where(
                        note.published.isTrue(),
                        note.deletedAt.isNull(),
                        keywordCondition(keyword, searchOption)
                );

        return PageableExecutionUtils.getPage(content.stream().map(NoteDto::from).toList(), pageable, countQuery::fetchOne);
    }

    private BooleanExpression keywordCondition(String keyword, NoteSearchOption searchOption) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        String trimmedKeyword = keyword.trim();

        return switch (searchOption) {
            case TITLE -> note.title.contains(trimmedKeyword);
            case CONTENT -> note.content.contains(trimmedKeyword);
            default -> note.title.contains(trimmedKeyword)
                    .or(note.content.contains(trimmedKeyword));
        };
    }
}
