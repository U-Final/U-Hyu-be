package com.ureca.uhyu.domain.auth.handler;

import com.ureca.uhyu.domain.auth.dto.CustomOAuth2User;
import com.ureca.uhyu.domain.auth.jwt.JwtTokenProvider;
import com.ureca.uhyu.domain.user.enums.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 *  로그인 성공 후 사용자 정보를 가져와서 토큰 생성 + 리다이랙트 경로 설정
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // JSESSIONID 제거
        }

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = customOAuth2User.getUserId();
        UserRole userRole = customOAuth2User.getUserRole();
        Boolean isNewUser = customOAuth2User.isNewUser();

        log.info("토큰 새로 생성");

        String accessToken = jwtTokenProvider.generateToken(String.valueOf(userId), userRole);
        String refreshToken = jwtTokenProvider.generateToken(String.valueOf(userId), userRole);

//        log.info("AccessToken = {}", accessToken);
//        log.info("RefreshToken = {}", refreshToken);

        // 만든 토큰 쿠키에 담기
        Cookie accessCookie = new Cookie("access_token", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) jwtTokenProvider.getAccessTokenExp());

        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) jwtTokenProvider.getRefreshTokenExp());

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // 신규/기존 유저에 따라 redirect
        String host = request.getHeader("host");
        String frontBaseUrl = "http://localhost:3000";

//        String frontBaseUrl = (host != null && host.contains("localhost"))
//                ? "http://localhost:3000"
//                : "https://ixiu.site";

        String redirectUrl = isNewUser
                ? frontBaseUrl + "/user/extra-info"
                : frontBaseUrl + "/main";

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirectUrl);
    }
}