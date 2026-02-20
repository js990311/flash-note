package com.rejs.flashnote.global.gemini.exception;

import com.rejs.flashnote.global.exception.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeminiErrorCode implements ErrorCode {
    INVALID_REQUEST("/error/gemini/invalid-request", "GEMINI_INVALID_REQUEST", HttpStatus.BAD_REQUEST, "AI 요청 파라미터가 올바르지 않습니다."),
    UNAUTHORIZED("/error/gemini/unauthorized", "GEMINI_UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "AI 인증 정보가 올바르지 않습니다."),
    PERMISSION_DENIED("/error/gemini/permission-denied", "GEMINI_PERMISSION_DENIED", HttpStatus.FORBIDDEN, "AI 호출 권한이 없습니다."),
    RATE_LIMIT("/error/gemini/rate-limit", "GEMINI_RATE_LIMIT", HttpStatus.TOO_MANY_REQUESTS, "AI 요청 한도를 초과했습니다. 잠시 후 다시 시도해 주세요."),
    SAFETY_BLOCKED("/error/gemini/safety-blocked", "GEMINI_SAFETY_BLOCKED", HttpStatus.UNPROCESSABLE_ENTITY, "AI 응답이 안전 정책에 의해 차단되었습니다."),
    UPSTREAM_TIMEOUT("/error/gemini/upstream-timeout", "GEMINI_UPSTREAM_TIMEOUT", HttpStatus.GATEWAY_TIMEOUT, "AI 서버 응답 시간이 초과되었습니다."),
    UPSTREAM_UNAVAILABLE("/error/gemini/upstream-unavailable", "GEMINI_UPSTREAM_UNAVAILABLE", HttpStatus.BAD_GATEWAY, "AI 서버와 통신 중 일시적인 문제가 발생했습니다."),
    RESPONSE_PARSING_FAILED("/error/gemini/response-parsing-failed", "GEMINI_RESPONSE_PARSING_FAILED", HttpStatus.BAD_GATEWAY, "AI 응답을 해석할 수 없습니다."),
    CONTEXT_NOT_FOUND("/error/gemini/context-not-found", "GEMINI_CONTEXT_NOT_FOUND", HttpStatus.INTERNAL_SERVER_ERROR, "AI 페르소나 설정을 찾을 수 없습니다."),
    UNKNOWN("/error/gemini/unknown", "GEMINI_UNKNOWN", HttpStatus.BAD_GATEWAY, "AI 서버 호출 중 알 수 없는 오류가 발생했습니다.");

    private final String type;
    private final String title;
    private final HttpStatus status;
    private final String detail;
}