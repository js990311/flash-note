package com.rejs.flashnote.domain.note.service;

import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.note.dto.NoteGroupListDto;
import com.rejs.flashnote.domain.note.dto.request.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.dto.NoteGroupDto;
import com.rejs.flashnote.domain.note.dto.request.UpdateNoteGroupRequest;
import com.rejs.flashnote.domain.note.entity.NoteGroup;
import com.rejs.flashnote.domain.note.entity.NotePermission;
import com.rejs.flashnote.domain.note.repository.MyNoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NoteGroupRepository;
import com.rejs.flashnote.domain.note.repository.NotePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteGroupService {
    private final NoteGroupRepository noteGroupRepository;
    private final NotePermissionRepository notePermissionRepository;
    private final MemberRepository memberRepository;
    private final MyNoteGroupRepository myNoteGroupRepository;

    // Create
    @Transactional
    public Long createNoteGroup(Long memberId, CreateNoteGroupRequest request){
        NoteGroup noteGroup = new NoteGroup(request.getName());
        Member member = memberRepository.getReferenceById(memberId);
        noteGroup = noteGroupRepository.save(noteGroup);

        NotePermission notePermission = NotePermission.createNoteGroup(member, noteGroup);
        notePermission = notePermissionRepository.save(notePermission);
        return noteGroup.getId();
    }

    // Read

    @Transactional(readOnly = true)
    public NoteGroupDto readById(Long noteGroupId){
        NoteGroup noteGroup = noteGroupRepository.findById(noteGroupId).orElseThrow();
        return NoteGroupDto.from(noteGroup);
    }

    @Transactional(readOnly = true)
    public Page<NoteGroupDto> readByPage(Pageable pageable){
        return noteGroupRepository.findAll(pageable).map(NoteGroupDto::from);
    }

    @Transactional(readOnly = true)
    public Page<NoteGroupListDto> readMyNoteGroupsByPage(Long memberId, Pageable pageable){
        return myNoteGroupRepository.findByMyPage(memberId, pageable);
    }

    // Update
    @Transactional
    public void updateName(Long noteGroupId, UpdateNoteGroupRequest request){
        NoteGroup noteGroup = noteGroupRepository.findById(noteGroupId).orElseThrow();
        noteGroup.updateName(request.getName());
    }

    // Delete
    @Transactional
    public void deleteNoteGroup(Long noteGroupId){
        NoteGroup noteGroup = noteGroupRepository.getReferenceById(noteGroupId);
        notePermissionRepository.deleteByNoteGroup(noteGroup);
        noteGroupRepository.delete(noteGroup);
    }

}
