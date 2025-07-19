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

        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = userRepository.findById(customOAuth2User.getUserId())
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));

        log.info("✅ 로그인 성공: userId={}, role={}", user.getId(), user.getUserRole());

        // 쿠키 수동 생성
        String cookieHeaderValue = tokenService.buildAccessTokenHeaderValue(
                String.valueOf(user.getId()), user.getUserRole()
        );
        log.info("✅ access_token Set-Cookie 헤더 설정: {}", cookieHeaderValue);
        response.addHeader("Set-Cookie", cookieHeaderValue);  // ✅ addHeader 로 수정

        tokenService.createRefreshToken(user);

        // 프론트 리다이렉트 주소 설정
        String redirectUrl = resolveRedirectUrl(request, user.getUserRole());
        log.info("✅ 리다이렉트 대상: {}", redirectUrl);

        // 리다이렉트 대신 JS로 리디렉션 처리
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
