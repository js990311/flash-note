package com.rejs.flashnote.domain.note.dto;

import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.domain.note.search.document.NoteDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class NoteDto {
    private Long id;
    private String title;
    private String content;
    private Instant updatedAt;
    private Long ownerId;
    private boolean published;

    public Long getMemberId(){
        return ownerId;
    }

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

    public static NoteDto from(NoteDocument note){
        return NoteDto.builder()
                .id(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .updatedAt(note.getUpdatedAt())
                .published(note.isPublished())
                .ownerId(note.getMemberId())
                .build();

    }
}
