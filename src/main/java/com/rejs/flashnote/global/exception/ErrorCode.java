package com.rejs.flashnote.global.exception;

import org.springframework.http.HttpStatus;

/**
 * ErrorCode 구현 후 ErrorCodeListController에 반드시 values를 추가하도록 할 것 model에
 */
public interface ErrorCode {
    String getType();
    String getTitle();
    HttpStatus getStatus();
    String getDetail();

    /*
    Lombok All args랑 getter 쓸 것
    private final String type;
    private final String title;
    private final HttpStatus status;
    private final String detail;
     */
}
