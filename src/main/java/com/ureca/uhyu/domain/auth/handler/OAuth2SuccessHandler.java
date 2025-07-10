package com.ureca.uhyu.domain.auth.handler;

import com.ureca.uhyu.domain.auth.dto.CustomOAuth2User;
import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
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

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // JSESSIONID 제거
        }

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = userRepository.findById(customOAuth2User.getUserId())
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));

        Long userId = customOAuth2User.getUserId();
        UserRole userRole = customOAuth2User.getUserRole();
        Boolean isNewUser = customOAuth2User.isNewUser();

        log.info("토큰 발급");

        Cookie accessCookie = tokenService.createAccessTokenCookie(String.valueOf(userId), userRole);
        tokenService.createRefreshToken(user);

        response.addCookie(accessCookie);

        // 신규/기존 유저에 따라 redirect
        String redirectUrl = resolveRedirectUrl(request, isNewUser);

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirectUrl);
    }

    private String resolveRedirectUrl(HttpServletRequest request, boolean isNewUser) {
        String frontBaseUrl = "http://localhost:3000";
        return isNewUser ? frontBaseUrl + "/user/extra-info" : frontBaseUrl + "/main";
    }
}