package com.rejs.flashnote.domain.note.repository;

import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteSearchOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteSearchRepository {
    Page<NoteDto> searchNote(Long memberId, String keyword, NoteSearchOption searchOption, Pageable pageable);
}
