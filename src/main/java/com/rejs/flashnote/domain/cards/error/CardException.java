package com.rejs.flashnote.domain.cards.error;

import com.rejs.flashnote.global.exception.BusinessException;
import com.rejs.flashnote.global.exception.ErrorCode;

public class CardException extends BusinessException {
    public CardException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CardException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public static CardException notFound(){
        return new CardException(CardErrorCode.NOT_FOUND);
    }
}
