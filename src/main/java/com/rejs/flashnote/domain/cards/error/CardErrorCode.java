package com.rejs.flashnote.domain.cards.error;

import com.rejs.flashnote.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardErrorCode implements ErrorCode {
    NOT_FOUND("/error/cards/not-found", "CARD_NOT_FOUND", HttpStatus.NOT_FOUND, "해당하는 카드가 없습니다.")
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
    private final String detail;
}
