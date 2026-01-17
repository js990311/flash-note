package com.rejs.flashnote.domain.note.dto;

import com.rejs.flashnote.domain.note.entity.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class NoteDto {
    private Long id;
    private Long groupId;
    private String title;
    private String content;

    public static NoteDto from(Note note){
        return new NoteDto(
                note.getId(),
                note.getGroup().getId(),
                note.getTitle(),
                note.getContent()
        );
    }
}
