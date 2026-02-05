package com.rejs.flashnote.global.exception.throwable;

import com.rejs.flashnote.global.exception.code.ErrorCode;

public class InvalidParameterException extends BusinessException{
    public InvalidParameterException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidParameterException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InvalidParameterException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }
}
