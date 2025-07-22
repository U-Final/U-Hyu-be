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
        boolean result = PermitAllURI.isPermit(uri);

        log.info("현재 URI : " +uri);
        log.info("😀 isPermit 여부 : " + result);

        return result;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT필터 진입");

        try {
            String accessToken = extractAccessTokenFromCookie(request);

            if (accessToken == null || accessToken.trim().isEmpty()) {
                log.warn("access_token이 비어있음");
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtTokenProvider.validateToken(accessToken)) {
                log.info("토큰 존재 && 토큰 validate");

                String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
                String role = jwtTokenProvider.getRoleFromToken(accessToken);
                log.info("현재 로그인 회원" + userId + "현재 회원의 role : " + role);

                if (role == null) {
                    throw new GlobalException(ResultCode.INVALID_ROLE_IN_TOKEN);
                }

                setAuthenticationContext(request, userId, role);

                log.info("doFilter로 다음 필터로 넘거갈거임");
                filterChain.doFilter(request, response);

                return;
            }
            log.info("토큰 존재 하지 않음 || 토큰 validate하지 않음");

            String expiredAccessToken = extractAccessTokenFromCookie(request);

            String userId = jwtTokenProvider.getUserIdFromExpiredToken(expiredAccessToken);

            log.info("만료된 access token의 userId : " + userId);

            String refreshToken = tokenRepository.findByUserId(Long.parseLong(userId))
                .map(token -> token.getRefreshToken())
                .orElse(null);

            log.info("리프레시 토큰 : " + refreshToken);
            log.info("jwtTokenProvider.validateToken(refreshToken) 결과 : " + jwtTokenProvider.validateToken(refreshToken));

            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                log.info("리프레시 토큰 존재 || 리프레시 토큰 validate");

                String userRoleString = jwtTokenProvider.getRoleFromToken(refreshToken);
                if (userRoleString == null) {
                    throw new GlobalException(ResultCode.INVALID_ROLE_IN_TOKEN);
                }
                UserRole userRole = UserRole.valueOf(userRoleString);

                String newAccessToken = jwtTokenProvider.generateToken(userId, userRole);

                Cookie newAccessTokenCookie = new Cookie("access_token", newAccessToken);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setMaxAge((int) ACCESS_TOKEN_EXP);
                response.addCookie(newAccessTokenCookie);

                setAuthenticationContext(request, userId, userRoleString);
                filterChain.doFilter(request, response);
                return;
            }

            log.info("리프레시 토큰 존재 안함 || 리프레시 토큰 validate 안함");

            response.sendRedirect("/login");

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
        log.info("setAuthenticationContext()이 실행되었음!!!");

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
