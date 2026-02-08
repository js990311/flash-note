package com.rejs.flashnote.domain.note.dto.request.note;


import com.rejs.flashnote.domain.note.dto.NoteDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteEditRequest {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    @NotNull
    private Boolean published;

    public static NoteEditRequest from(NoteDto noteDto) {
        return NoteEditRequest.builder()
                .title(noteDto.getTitle())
                .content(noteDto.getContent())
                .published(noteDto.isPublished())
                .build();
    }
}
