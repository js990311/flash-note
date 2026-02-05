package com.rejs.flashnote.domain.note.error;

import com.rejs.flashnote.global.exception.throwable.BusinessException;
import com.rejs.flashnote.global.exception.code.ErrorCode;

public class NoteException extends BusinessException {
    public NoteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NoteException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public static NoteException notFound(){
        return new NoteException(NoteErrorCode.NOT_FOUND);
    }

}
