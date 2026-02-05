package com.rejs.flashnote.global.exception.controller.response;

import com.rejs.flashnote.global.exception.code.ErrorCode;
import com.rejs.flashnote.global.exception.throwable.InvalidParameterException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final String type;
    private final String title;
    private final int status;
    private final Object detail;
    private final String instance;

    public static ErrorResponse from(ErrorCode errorCode, String instance){
        return ErrorResponse.builder()
                .type(errorCode.getType())
                .title(errorCode.getTitle())
                .status(errorCode.getStatus().value())
                .detail(errorCode.getDetail())
                .instance(instance)
                .build();
    }

    public static ErrorResponse invalidParameter(InvalidParameterException ex, String instance){
        return ErrorResponse.builder()
                .type(ex.getErrorCode().getType())
                .title(ex.getErrorCode().getTitle())
                .status(ex.getErrorCode().getStatus().value())
                .detail(ex.getInvalidParameters())
                .instance(instance)
                .build();
    }
}
