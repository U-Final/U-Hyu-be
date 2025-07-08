package com.ureca.uhyu.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommonResponse<T> {

    @Schema(description = "응답 상태 코드 (ResultCode에 정의된 code 값)")
    private final Integer statusCode;

    @Schema(description = "응답 메시지 (ResultCode에 정의된 메시지 또는 예외 메시지)")
    private final String message;

    @Schema(description = "실제 응답 데이터")
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // null 또는 빈 객체일 경우 제외
    private T data;

    /**
     * 데이터 없이 ResultCode만으로 응답을 생성할 때 사용 (예: 에러 응답, 삭제 성공 등)
     */
    public CommonResponse(ResultCode resultCode) {
        this.statusCode = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 데이터와 함께 성공 응답을 생성할 때 사용
     */
    public CommonResponse(ResultCode resultCode, T data) {
        this.statusCode = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    /**
     * 커스텀 메시지를 포함한 응답을 생성할 때 사용 (예: validation 에러 등)
     */
    private CommonResponse(Integer statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    /**
     * 성공 응답 생성 - 기본 메시지(ResultCode.SUCCESS) 사용
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(ResultCode.SUCCESS, data);
    }

    /**
     * 성공 응답 생성 - 사용자 정의 ResultCode 사용
     */
    public static <T> CommonResponse<T> success(ResultCode resultCode, T data) {
        return new CommonResponse<>(resultCode, data);
    }

    /**
     * 실패 응답 생성 - 커스텀 메시지를 포함함 (예: Validation 에러 메시지)
     */
    public static <T> CommonResponse<T> fail(ResultCode resultCode, String message) {
        return new CommonResponse<>(resultCode.getCode(), message, null);
    }
}
