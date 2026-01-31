package com.rejs.flashnote.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class BusinessExceptionController {

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode ec = e.getErrorCode();
        log.error("Business Error: [{}] {} - {}", ec.getType(), ec.getTitle(), e.getMessage());
        return createErrorModelAndView(ec, e.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUncaughtException(Exception e, HttpServletRequest request) {
        log.error("Uncaught Exception: ", e);
        ErrorCode ec = CommonErrorCode.INTERNAL_SERVER_ERROR;
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

}
