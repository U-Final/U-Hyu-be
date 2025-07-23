package com.ureca.uhyu.domain.auth.handler;

import com.ureca.uhyu.domain.auth.dto.CustomOAuth2User;
import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.frontend.url.local}")
    private String localFrontendUrl;

    @Value("${app.frontend.url.prod}")
    private String prodFrontendUrl;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 요청 정보 로깅
        log.debug("=== OAuth2 성공 핸들러 시작 ===");
        log.debug("Request URL: {}", request.getRequestURL());

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 인증된 사용자 조회
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = userRepository.findById(customOAuth2User.getUserId())
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));

        log.debug("✅ 로그인 성공: userId={}, role={}", user.getId(), user.getUserRole());

        // AccessToken 쿠키 생성 및 추가
        tokenService.addAccessTokenCookie(response, String.valueOf(user.getId()), user.getUserRole());

        // RefreshToken DB 저장
        tokenService.saveRefreshToken(user);

        // 리다이렉트 URL 결정
        String redirectUrl = resolveRedirectUrl(request, user.getUserRole());
        log.debug("✅ 최종 리다이렉트 대상: {}", redirectUrl);

        // 서버 사이드 리다이렉트
        response.sendRedirect(redirectUrl);
        log.debug("=== OAuth2 성공 핸들러 완료 ===");
    }

    private String resolveRedirectUrl(HttpServletRequest request, UserRole userRole) {
        String host = request.getHeader("host");
        log.info("Request host header: '{}'", host);

        // 환경에 따른 프론트엔드 URL 결정
        String frontBaseUrl = host!=null && host.contains("localhost") ? localFrontendUrl : prodFrontendUrl;

        // 사용자 역할에 따른 리다이렉트 경로 결정
        String finalRedirectUrl = userRole== UserRole.TMP_USER ? frontBaseUrl + "/user/extra-info" : frontBaseUrl;

        log.debug("Final redirect URL: '{}'", finalRedirectUrl);
        return finalRedirectUrl;
    }
}
