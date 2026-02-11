package com.rejs.flashnote.domain.note.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.global.meilisearch.config.initializer.annotation.Filterable;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Builder
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

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
