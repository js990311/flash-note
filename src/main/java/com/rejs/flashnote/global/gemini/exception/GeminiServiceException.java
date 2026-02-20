package com.rejs.flashnote.global.gemini.exception;

import com.rejs.flashnote.global.exception.throwable.BusinessException;

public class GeminiServiceException extends BusinessException {
    public GeminiServiceException(GeminiErrorCode errorCode) {
        super(errorCode);
    }

    public GeminiServiceException(GeminiErrorCode errorCode, Throwable cause) {
        super(cause, errorCode);
    }
}