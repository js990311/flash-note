package com.rejs.flashnote.domain.note.dto.request.note;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNoteRequest {
    @NotNull
    private Long noteGroupId;
}
