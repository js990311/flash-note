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
    private String title;
    private String content;
    private LocalDateTime updatedAt;
    private Long ownerId;
    private boolean published;

    public static NoteDto from(Note note){
        return NoteDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .updatedAt(note.getUpdatedAt())
                .published(note.isPublished())
                .ownerId(note.getMember().getId())
                .build();
    }
}
