package com.ureca.uhyu.global.response;

/**
 * 공통 예외 응답 DTO
 * @param name 발생한 예외 ENUM의 이름(ex. USER_NOT_FOUND)
 * @param message 발생한 예외 메시지
 */
public record ExceptionRes(String name, String message) {

}
