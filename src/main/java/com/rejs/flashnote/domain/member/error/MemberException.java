package com.rejs.flashnote.domain.member.error;

import com.rejs.flashnote.global.exception.code.ErrorCode;
import com.rejs.flashnote.global.exception.throwable.BusinessException;

public class MemberException extends BusinessException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MemberException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public static MemberException notFound(){
        return new MemberException(MemberErrorCode.NOT_FOUND);
    }

}
