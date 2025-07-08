package com.ureca.uhyu.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResultCode {
    // 1000번대 (글로벌)
    SUCCESS(HttpStatus.OK, 0, "정상 처리 되었습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 1000, "잘못된 입력값입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 1003, "사용자를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 1006, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 1007, "권한이 없는 사용자입니다."),

    // 2000번대 (인증/인가 관련)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 2000, "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, 2001, "Refresh Token이 필요합니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2002, "Refresh Token이 만료되었습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2003, "Access Token이 만료되었습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, 2004, "토큰 재발급에 성공했습니다. "),
    LOGOUT_SUCCESS(HttpStatus.OK, 2005, "로그아웃에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.OK, 2006, "회원가입을 위한 추가정보 입력에 성공했습니다.");

    // 3000번대 (지도 관련)

    private final HttpStatus status;
    private final int code;
    private final String message;
}
