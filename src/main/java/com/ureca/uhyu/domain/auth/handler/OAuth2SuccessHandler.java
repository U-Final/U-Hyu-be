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

        // ✅ 기존 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        // ✅ 인증된 사용자 조회
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = userRepository.findById(customOAuth2User.getUserId())
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));

        log.info("✅ 로그인 성공: userId={}, role={}", user.getId(), user.getUserRole());

        // ✅ AccessToken 쿠키 생성 및 추가
        tokenService.addAccessTokenCookie(response, String.valueOf(user.getId()), user.getUserRole());

        // ✅ RefreshToken DB 저장
        tokenService.addRefreshTokenCookie(user);

        // ✅ 리다이렉트 경로 결정
        String redirectUrl = resolveRedirectUrl(request, user.getUserRole());
        log.info("✅ 리다이렉트 대상: {}", redirectUrl);

        // ✅ JS 리다이렉션 방식
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("<script>window.location.href='" + redirectUrl + "'</script>");
    }

    private String resolveRedirectUrl(HttpServletRequest request, UserRole userRole) {
        String host = request.getHeader("host");
        String frontBaseUrl = (host != null && host.contains("localhost"))
                ? "http://localhost:8080"
                : "https://www.u-hyu.site";
        if (userRole == UserRole.TMP_USER) {
            return frontBaseUrl + "/user/extra-info";
        } else {
            return frontBaseUrl;
        }
    }
}
