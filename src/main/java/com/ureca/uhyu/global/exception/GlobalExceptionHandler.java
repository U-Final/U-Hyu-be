package com.ureca.uhyu.global.exception;

import com.ureca.uhyu.global.response.ExceptionRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler     {
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ExceptionRes> handleGeneralException(GeneralException e) {

        return ResponseEntity.status(e.getHttpStatus())
                .body(new ExceptionRes(e.getExceptionName(), e.getMessage()));
    }
}
