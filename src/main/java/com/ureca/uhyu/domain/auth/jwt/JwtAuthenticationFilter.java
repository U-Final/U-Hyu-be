package com.ureca.uhyu.domain.auth.jwt;

import com.ureca.uhyu.domain.auth.dto.CustomUserDetails;
import com.ureca.uhyu.domain.auth.repository.TokenRepository;
import com.ureca.uhyu.domain.auth.service.CustomUserDetailsService;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.global.config.PermitAllURI;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.access-token-expiration-time}")
    private long ACCESS_TOKEN_EXP;

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    // spring security의 인증 필터와 맞춰 줘야 함
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return PermitAllURI.isPermit(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = extractAccessTokenFromCookie(request);
            if (accessToken == null) {
                log.warn("🚫 access_token 쿠키가 요청에 포함되어 있지 않습니다.");
            } else {
                log.info("📦 들어온 access_token: {}", accessToken);
            }

            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {

                log.info("✅ access_token 유효함");

                String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
                String role = jwtTokenProvider.getRoleFromToken(accessToken);

                if (role == null) {
                    throw new GlobalException(ResultCode.INVALID_ROLE_IN_TOKEN);
                }

                setAuthenticationContext(request, userId, role);
                filterChain.doFilter(request, response);
                return;
            }

            String expiredAccessToken = extractAccessTokenFromCookie(request);
            log.warn("📛 access_token이 유효하지 않거나 만료됨. expiredAccessToken 재시도: {}", expiredAccessToken);

            String userId = jwtTokenProvider.getUserIdFromExpiredToken(expiredAccessToken);

            String refreshToken = tokenRepository.findTokenByUserId(Long.parseLong(userId))
                .map(token -> token.getRefreshToken())
                .orElse(null);

            log.info("📦 추출된 refresh 토큰: {}", refreshToken);

            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                String roleString = jwtTokenProvider.getRoleFromToken(refreshToken);
                if (roleString == null) {
                    throw new GlobalException(ResultCode.INVALID_ROLE_IN_TOKEN);
                }

                UserRole userRole = UserRole.valueOf(roleString);
                String newAccessToken = jwtTokenProvider.generateToken(userId, userRole);
                log.info("🔁 새 access_token 발급 완료: {}", newAccessToken);
                Cookie newAccessTokenCookie = new Cookie("access_token", newAccessToken);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setMaxAge((int) ACCESS_TOKEN_EXP);
                response.addCookie(newAccessTokenCookie);

                setAuthenticationContext(request, userId, roleString);
                filterChain.doFilter(request, response);
                return;
            }

            response.sendRedirect("/login");
            return;

        } catch (ExpiredJwtException e) {
            log.warn("❌ 토큰 만료 예외 발생: {}", e.getMessage());
            response.sendRedirect("/login");
        } catch (IllegalArgumentException e) {
            log.error("❌ 잘못된 JWT 토큰 포맷: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            log.error("❌ JwtAuthenticationFilter 예외 발생", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // SecurityContext에 인증 정보를 설정하고 다음 필터로 진행
    private void setAuthenticationContext(HttpServletRequest request, String userId, String role) {
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userId);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractTokenFromCookie(HttpServletRequest request, String tokenName) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (tokenName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        return extractTokenFromCookie(request, "access_token");
    }
}
