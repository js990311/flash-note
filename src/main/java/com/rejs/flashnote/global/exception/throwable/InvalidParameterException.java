package com.rejs.flashnote.global.exception.throwable;

import com.rejs.flashnote.global.exception.code.CommonErrorCode;
import com.rejs.flashnote.global.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class InvalidParameterException extends BusinessException{
    private final List<Map<String, String>> invalidParameters;

    public InvalidParameterException(List<Map<String, String>> invalidParameters) {
        super(CommonErrorCode.INVALID_PARAMETER_ERROR);
        this.invalidParameters = invalidParameters;
    }

    public static InvalidParameterException from(BindingResult bindingResult){
        List<Map<String, String>> invalidParameters = bindingResult.getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "reason", Objects.requireNonNullElse(error.getDefaultMessage(), "")
                )).toList();
        return new InvalidParameterException(invalidParameters);
    }
}
