package com.rejs.flashnote.domain.note.search.repository;

import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.NoteSummaryDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteSearchOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NoteSearchRepository {
    Slice<NoteSummaryDto> searchMyNote(Long memberId, String keyword, NoteSearchOption searchOption, Pageable pageable);

    Slice<NoteSummaryDto> searchPublicNote(String keyword, NoteSearchOption searchOption, Pageable pageable);

}
