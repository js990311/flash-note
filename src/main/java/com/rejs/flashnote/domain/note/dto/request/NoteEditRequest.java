package com.rejs.flashnote.domain.note.dto.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
public class NoteEditRequest {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
