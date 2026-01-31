package com.rejs.flashnote.domain.decks.error;

import com.rejs.flashnote.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum DeckErrorCode implements ErrorCode {
    NOT_FOUND("/error/decks/not-found", "DECK_NOT_FOUND", HttpStatus.NOT_FOUND, "해당하는 덱이 없습니다.")
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
    private final String detail;

}
