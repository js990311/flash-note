package com.rejs.flashnote.domain.note.search.document;

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
public class NoteDocument {
    private Long noteId;
    private String title;
    private String content;

    @Filterable
    private Long memberId;

    @Builder.Default
    @Filterable
    private boolean published = false;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public static NoteDocument from(Note note){
        return NoteDocument.builder()
                .noteId(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .memberId(note.getMember().getId())
                .published(note.isPublished())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .deletedAt(note.getDeletedAt())
                .build();
    }
}
