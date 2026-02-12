package com.rejs.flashnote.domain.note.search.repository.impl;

import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteSearchOption;
import com.rejs.flashnote.domain.note.search.document.NoteDocument;
import com.rejs.flashnote.domain.note.search.repository.NoteSearchRepository;
import com.rejs.flashnote.global.meilisearch.template.MeilisearchQuery;
import com.rejs.flashnote.global.meilisearch.template.MeilisearchTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class NoteSearchMeilisearchRepository implements NoteSearchRepository {
    private final MeilisearchTemplate meilisearchTemplate;

    @Override
    public Page<NoteDto> searchMyNote(Long memberId, String keyword, NoteSearchOption searchOption, Pageable pageable) {
        List<String> targetFields = resolveSearchAttributes(searchOption);
        return meilisearchTemplate.search(NoteDocument.class,
                MeilisearchQuery.builder()
                        .query(keyword)
                        .filter("memberId = " + memberId)
                        .searchAttributes(targetFields)
                        .pageable(pageable)
                        .build()
                ).map(NoteDto::from);
    }

    @Override
    public Page<NoteDto> searchPublicNote(String keyword, NoteSearchOption searchOption, Pageable pageable) {
        List<String> targetFields = resolveSearchAttributes(searchOption);
        return meilisearchTemplate.search(NoteDocument.class,
                MeilisearchQuery.builder()
                        .query(keyword)
                        .filter("published = true")
                        .searchAttributes(targetFields)
                        .pageable(pageable)
                        .build()
        ).map(NoteDto::from);
    }

    private List<String> resolveSearchAttributes(NoteSearchOption option) {
        if (option == null) {
            return Collections.emptyList(); // 전체 검색
        }
        return switch (option) {
            case TITLE -> List.of("title");
            case CONTENT-> List.of("content");
            default -> List.of("title", "content");
        };
    }
}
