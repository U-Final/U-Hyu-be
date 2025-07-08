package com.ureca.uhyu.global.exception;

import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // GlobalException 발생 시 반환 형태
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<CommonResponse<Void>> handleException(GlobalException e) {
        return ResponseEntity.status(e.getResultCode().getStatus())
            .body(new CommonResponse<>(e.getResultCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("유효하지 않은 요청입니다.");

        return ResponseEntity.badRequest()
                .body(CommonResponse.fail(ResultCode.INVALID_INPUT, message));
    }
}
