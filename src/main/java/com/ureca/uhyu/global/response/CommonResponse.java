package com.ureca.uhyu.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommonResponse<T> {

    @Schema(description = "http Status code")
    private final Integer statusCode;
    @Schema(description = "http status 메시지")
    private final String message;

    @Schema(description = "데이터")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;

    // 오류 등 데이터 없는 경우 사용
    public CommonResponse(ResultCode resultCode) {
        this.statusCode = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    // 성공 시 일반적인 생성자
    public CommonResponse(ResultCode resultCode, T data) {
        this.statusCode = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    // 유효성 검사 실패 시 커스텀 메시지
    private CommonResponse(Integer statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }


    // 이거 호출로 성공 생성자 자동 호출, 데이터 담아서 반환됨
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(ResultCode.SUCCESS, data);
    }

    public static <T> CommonResponse<T> success(ResultCode resultCode, T data) {
        return new CommonResponse<>(resultCode, data);
    }

    // 실패 응답 시
    public static <T> CommonResponse<T> fail(ResultCode resultCode, String message) {
        return new CommonResponse<>(resultCode.getCode(), message, null);
    }
}