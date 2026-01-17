package com.rejs.flashnote.domain.note.service;

import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.entity.NoteGroup;
import com.rejs.flashnote.domain.note.repository.MyNoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteGroupRepository noteGroupRepository;
    private final MyNoteGroupRepository myNoteGroupRepository;

    // ## Create

    /**
     * 노트 생성
     * - 노트의 편집과정에서 자동편집하도록 작성할 것이므로.
     * @param noteGroupId noteGroupId에 노트를 생성
     * @return 생성된 노트의 번호
     */
    @Transactional
    public Long createNote(Long noteGroupId){
        NoteGroup noteGroup = noteGroupRepository.getReferenceById(noteGroupId);
        Note note = Note.newNote(noteGroup);
        return noteRepository.save(note).getId();
    }

    // ## Read
    @Transactional(readOnly = true)
    public NoteDto readById(Long noteId){
        Note note = noteRepository.findById(noteId).orElseThrow();
        return NoteDto.from(note);
    }

    @Transactional(readOnly = true)
    public Page<NoteDto> readPageByNoteGroupId(Long noteGroupId, Pageable pageable){
        return myNoteGroupRepository.findNoteByNoteGroupId(noteGroupId,pageable);
    }

    // ## Update
    @Transactional
    public Long updateNote(Long noteId, NoteEditRequest request){
        Note note = noteRepository.findById(noteId).orElseThrow();
        note.edit(request.getTitle(), request.getContent());
        return note.getId();
    }

    // ## Delete
    @Transactional
    public Long deleteNote(Long noteId){
        Note note = noteRepository.findById(noteId).orElseThrow();
        noteRepository.delete(note);
        return note.getGroup().getId();
    }
}
