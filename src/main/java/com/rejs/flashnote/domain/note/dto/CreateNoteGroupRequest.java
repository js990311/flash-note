package com.rejs.flashnote.domain.note.dto;


import jakarta.validation.constraints.NotEmpty;

public record CreateNoteGroupRequest (
        @NotEmpty
        String name
){
        public CreateNoteGroupRequest() {
                this("");
        }
}
