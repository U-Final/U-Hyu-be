package com.ureca.uhyu.global.exception;

import org.springframework.http.HttpStatus;

public interface BaseException {

    String getExceptionName();
    HttpStatus getHttpStatus();
    String getMessage();
}
