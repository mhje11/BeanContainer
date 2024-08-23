package com.beancontainer.global.handler;

import com.beancontainer.global.exception.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
@Getter
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException ex) {
        log.error("CustomException: {}", ex.getMessage());
        return createErrorResponse(ex.getExceptionCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex) {
        if (ex instanceof NoResourceFoundException && ((NoResourceFoundException) ex).getResourcePath().equals("/favicon.ico")) {
            return ResponseEntity.notFound().build();
        }
        log.error("Unexpected error occurred", ex);
        return createErrorResponse(ExceptionCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> createErrorResponse(ExceptionCode exceptionCode) {
        ExceptionResponse response = new ExceptionResponse(exceptionCode.getCode(), exceptionCode.getMessage());
        return new ResponseEntity<>(response, exceptionCode.getHttpStatus());
    }

    @Getter
    private static class ExceptionResponse {
        private final int code;
        private final String message;

        public ExceptionResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

    }
}