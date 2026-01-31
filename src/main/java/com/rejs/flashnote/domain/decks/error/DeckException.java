package com.rejs.flashnote.domain.decks.error;

import com.rejs.flashnote.domain.cards.error.CardErrorCode;
import com.rejs.flashnote.global.exception.BusinessException;
import com.rejs.flashnote.global.exception.ErrorCode;

public class DeckException extends BusinessException {
    public DeckException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DeckException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public static DeckException notFound(){
        return new DeckException(DeckErrorCode.NOT_FOUND);
    }

}
