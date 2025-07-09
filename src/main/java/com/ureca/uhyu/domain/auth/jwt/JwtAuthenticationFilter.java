package com.ureca.uhyu.domain.auth.jwt;

import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.global.config.PermitAllURI;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// jwt access token 에 대한 인가 확인 필터
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.access-token-expiration-time}")
    private long ACCESS_TOKEN_EXP;

    private final JwtTokenProvider jwtTokenProvider;

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

            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {

                log.info("❗ access token 유효");

                String userId = jwtTokenProvider.getUserIdFromToken(accessToken).toString();
                String role = jwtTokenProvider.getRoleFromToken(accessToken);

                if (role == null) {
                    log.error("❌ access token에서 role을 추출하지 못했습니다. token: {}", accessToken);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증 실패 : 401에러
                    return; // 필터
                }

                log.info("✅ access token 인증 성공 - userId: {}, role: {}", userId, role);

                // SecurityContext에 인증 정보를 설정하고 다음 필터로 진행
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of(new SimpleGrantedAuthority(role)));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
                return;
            }

            log.info("❌ access token 인증 실패 - 재발급 시도");
            log.info("❗ refresh token 유효성 검사");

            String refreshToken = extractRefreshTokenFromCookie(request);

            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {

                log.info("✅ refresh 토큰 유효");

                String userId = jwtTokenProvider.getUserIdFromToken(refreshToken).toString();
                String roleString = jwtTokenProvider.getRoleFromToken(refreshToken);

                UserRole userRole = UserRole.valueOf(roleString);

                String newAccessToken = jwtTokenProvider.generateToken(userId, userRole);
                Cookie newAccessTokenCookie = new Cookie("access_token", newAccessToken);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setMaxAge((int) ACCESS_TOKEN_EXP);
                response.addCookie(newAccessTokenCookie);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(roleString)));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("♻️ access token 재발급 완료 - userId: {}", userId);

                filterChain.doFilter(request, response);
                return;
            }

            log.warn("❌ access + refresh token 모두 유효하지 않음 -> 로그인하지 않은 사용자 : 로그인화면 리다이렉트");
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

    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("refresh_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

