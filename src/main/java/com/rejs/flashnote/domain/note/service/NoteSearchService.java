package com.rejs.flashnote.domain.note.service;

import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteSearchOption;
import com.rejs.flashnote.domain.note.search.repository.NoteSearchRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NoteSearchService {
    private final NoteSearchRepository noteSearchRepository;

    @Observed(name = "note.service.search.me")
    @Transactional(readOnly = true)
    public Slice<NoteDto> searchMyNote(Long memberId, String keyword, NoteSearchOption searchOption, Pageable pageable){
        return noteSearchRepository.searchMyNote(memberId, keyword, searchOption, pageable);
    }

    @Observed(name = "note.service.search.public")
    @Transactional(readOnly = true)
    public Slice<NoteDto> searchPublicNote(String keyword, NoteSearchOption searchOption, Pageable pageable){
        return noteSearchRepository.searchPublicNote(keyword, searchOption, pageable);
    }


}
