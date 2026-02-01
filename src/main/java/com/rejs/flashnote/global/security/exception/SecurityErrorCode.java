package com.rejs.flashnote.global.security.exception;

import com.rejs.flashnote.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SecurityErrorCode implements ErrorCode {
    ;
    private final String type;
    private final String title;
    private final HttpStatus status;
    private final String detail;

}
