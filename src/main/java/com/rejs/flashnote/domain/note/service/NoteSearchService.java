package com.rejs.flashnote.domain.note.service;

import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteSearchOption;
import com.rejs.flashnote.domain.note.repository.NoteSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NoteSearchService {
    private final NoteSearchRepository noteSearchRepository;

    @Transactional(readOnly = true)
    public Page<NoteDto> readById(Long memberId, String keyword, NoteSearchOption searchOption, Pageable pageable){
        return noteSearchRepository.searchNote(memberId, keyword, searchOption, pageable);
    }
}
