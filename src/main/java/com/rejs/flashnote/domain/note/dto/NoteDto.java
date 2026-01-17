package com.rejs.flashnote.domain.note.dto;

import com.rejs.flashnote.domain.note.entity.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class NoteDto {
    private Long id;
    private Long groupId;
    private String title;
    private String content;
    private LocalDateTime updateAt;

    public static NoteDto from(Note note){
        return NoteDto.builder()
                .id(note.getId())
                .groupId(note.getGroup().getId())
                .title(note.getTitle())
                .content(note.getContent())
                .updateAt(note.getUpdatedAt())
                .build();
    }
}
