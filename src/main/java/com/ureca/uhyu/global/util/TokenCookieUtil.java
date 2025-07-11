package com.ureca.uhyu.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class TokenCookieUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    private TokenCookieUtil() {
        // static 유틸 클래스의 인스턴스 생성을 방지
    }

    public static String extractAccessToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void expireAccessTokenCookie(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true); // HTTPS 환경에서만 전달될 수 있도록
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(expiredCookie);
    }
}