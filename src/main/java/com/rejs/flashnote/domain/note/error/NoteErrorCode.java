package com.rejs.flashnote.domain.note.error;

import com.rejs.flashnote.global.exception.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum NoteErrorCode implements ErrorCode {
    NOT_FOUND("/error/notes/not-found", "NOTE_NOT_FOUND", HttpStatus.NOT_FOUND, "해당하는 노트가 없습니다.")
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
    private final String detail;

}
