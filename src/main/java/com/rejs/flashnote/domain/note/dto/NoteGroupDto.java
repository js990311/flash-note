package com.rejs.flashnote.domain.note.dto;

import com.rejs.flashnote.domain.note.entity.NoteGroup;
import lombok.Getter;

@Getter
public class NoteGroupDto {
    private Long id;
    private String name;

    public NoteGroupDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static NoteGroupDto from(NoteGroup noteGroup){
        return new NoteGroupDto(noteGroup.getId(), noteGroup.getName());
    }
}
