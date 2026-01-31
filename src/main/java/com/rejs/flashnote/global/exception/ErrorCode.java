package com.rejs.flashnote.global.exception;

import org.springframework.http.HttpStatus;

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
