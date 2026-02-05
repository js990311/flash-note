package com.rejs.flashnote.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode{
    INTERNAL_SERVER_ERROR("/error/common/internal_server_error", "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 문제가 발생했습니다."),
    INVALID_PARAMETER_ERROR("/error/common/invalid_parameter_error", "INVALID_PARAMETER_ERROR", HttpStatus.BAD_REQUEST, "입력을 잘못하셨습니다. ")
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
    private final String detail;

}
