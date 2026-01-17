package com.rejs.flashnote.domain.note.dto.request;


import jakarta.validation.constraints.NotEmpty;
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
}
