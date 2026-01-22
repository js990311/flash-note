package com.rejs.flashnote.domain.note.service;

import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.repository.MyNoteGroupRepository;
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
    private final MyNoteGroupRepository myNoteGroupRepository;
    private final MemberRepository memberRepository;

    // ## Create

    @Transactional
    public Long createNote(Long memberId){
        Member member = memberRepository.getReferenceById(memberId);
        Note note = Note.newNote(member);
        return noteRepository.save(note).getId();
    }

    // ## Read
    @Transactional(readOnly = true)
    public NoteDto readById(Long noteId){
        Note note = noteRepository.findById(noteId).orElseThrow();
        return NoteDto.from(note);
    }

    @Transactional(readOnly = true)
    public Page<NoteDto> readByPage(Long memberId, Pageable pageable){
        return myNoteGroupRepository.findByMemberId(memberId, pageable);
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
    public void deleteNote(Long noteId){
        Note note = noteRepository.findById(noteId).orElseThrow();
        noteRepository.delete(note);
    }
}
