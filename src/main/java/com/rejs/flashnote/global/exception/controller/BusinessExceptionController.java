package com.rejs.flashnote.global.exception.controller;

import com.rejs.flashnote.global.exception.controller.response.ErrorResponse;
import com.rejs.flashnote.global.exception.throwable.BusinessException;
import com.rejs.flashnote.global.exception.code.CommonErrorCode;
import com.rejs.flashnote.global.exception.code.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class BusinessExceptionController {

    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode ec = e.getErrorCode();
        log.error("Business Error: [{}] {} - {}", ec.getType(), ec.getTitle(), e.getMessage());
        if(isAjax(request)){
            return createErrorResponseEntity(ec, request);
        }
        return createErrorModelAndView(ec, e.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public Object handleUncaughtException(Exception e, HttpServletRequest request) {
        log.error("Uncaught Exception: ", e);
        ErrorCode ec = CommonErrorCode.INTERNAL_SERVER_ERROR;
        if(isAjax(request)){
            return createErrorResponseEntity(ec, request);
        }
        return createErrorModelAndView(ec, ec.getDetail(), request);
    }

    private ModelAndView createErrorModelAndView(ErrorCode ec, String message, HttpServletRequest request) {
        HttpStatus status = ec.getStatus();
        String viewGroup = status.is4xxClientError() ? "4xx" : "5xx";

        ModelAndView mav = new ModelAndView("error/" + viewGroup);
        mav.addObject("type", ec.getType());
        mav.addObject("title", ec.getTitle());
        mav.addObject("status", status.value());
        mav.addObject("statusName", status.name());
        mav.addObject("detail", message);
        mav.addObject("instance", request.getRequestURI());

        mav.setStatus(status);
        return mav;
    }

    private boolean isAjax(HttpServletRequest request){
        String accept = request.getHeader("Accept");
        String contentType = request.getHeader("Content-Type");

        return (accept != null && accept.contains("application/json")) ||
                (contentType != null && contentType.contains("application/json"));
    }

    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode ec, HttpServletRequest request){
        return ResponseEntity.status(ec.getStatus()).body(ErrorResponse.from(ec, request.getRequestURI()));
    }

}
