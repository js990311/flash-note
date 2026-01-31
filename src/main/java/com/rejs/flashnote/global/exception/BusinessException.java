package com.rejs.flashnote.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    public BusinessException(Throwable cause, ErrorCode errorCode) {
        super(errorCode.getDetail(), cause);
        this.errorCode = errorCode;
    }
}
