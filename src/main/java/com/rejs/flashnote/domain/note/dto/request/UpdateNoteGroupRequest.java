package com.rejs.flashnote.domain.note.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNoteGroupRequest {
    @NotEmpty(message = "이름은 공백일 수 없습니다.")
    private String name;
}
