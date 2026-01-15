package com.rejs.flashnote.domain.note.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 이름 바꾸기
 */
@Getter
@AllArgsConstructor
public class UpdateNoteGroupRequest {
    @NotEmpty(message = "이름은 공백일 수 없습니다.")
    private String name;
}
