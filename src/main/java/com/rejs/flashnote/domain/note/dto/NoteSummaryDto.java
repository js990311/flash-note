package com.rejs.flashnote.domain.note.dto;

import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.global.meilisearch.config.initializer.annotation.Filterable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NoteSummaryDto {
    private Long noteId;
    private String title;

    @Filterable
    private Long memberId;

    @Builder.Default
    @Filterable
    private boolean published = false;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Long getId(){
        return noteId;
    }

    public static NoteSummaryDto from(Note note){
        return NoteSummaryDto.builder()
                .noteId(note.getId())
                .title(note.getTitle())
                .memberId(note.getMember().getId())
                .published(note.isPublished())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .deletedAt(note.getDeletedAt())
                .build();
    }

    public static NoteSummaryDto from(NoteDto note){
        return NoteSummaryDto.builder()
                .noteId(note.getId())
                .title(note.getTitle())
                .memberId(note.getOwnerId())
                .published(note.isPublished())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

}
