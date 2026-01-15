package com.rejs.flashnote.domain.note.dto.request;


import jakarta.validation.constraints.NotEmpty;

public record CreateNoteGroupRequest (
        @NotEmpty(message = "노트그룹엔 이름이 있어야합니다.")
        String name
){
        public CreateNoteGroupRequest() {
                this("");
        }
}
