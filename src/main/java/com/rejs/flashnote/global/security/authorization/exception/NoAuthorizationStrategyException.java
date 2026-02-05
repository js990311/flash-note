package com.rejs.flashnote.global.security.authorization.exception;

import com.rejs.flashnote.global.exception.throwable.BusinessException;
import com.rejs.flashnote.global.exception.code.CommonErrorCode;

public class NoAuthorizationStrategyException extends BusinessException {
    public NoAuthorizationStrategyException(String domainType, String methodType) {
        super(String.format("해당하는 인가 처리 전략이 없습니다. domainType=%s, methodType=%s", domainType, methodType), CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
}
