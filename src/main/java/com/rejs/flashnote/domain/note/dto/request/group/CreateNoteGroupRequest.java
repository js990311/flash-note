package com.rejs.flashnote.domain.note.dto.request.group;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoteGroupRequest {
        @NotEmpty(message = "노트그룹엔 이름이 있어야합니다.")
        private String name;
}
