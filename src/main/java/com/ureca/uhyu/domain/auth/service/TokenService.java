package com.ureca.uhyu.domain.auth.service;

import com.ureca.uhyu.domain.auth.entity.Token;
import com.ureca.uhyu.domain.auth.jwt.JwtTokenProvider;
import com.ureca.uhyu.domain.auth.repository.TokenRepository;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenExpMillis;

    @Value("${jwt.cookie.domain}")
    private String cookieDomain;

    @Value("${jwt.cookie.secure}")
    private boolean isSecure;

    @Value("${jwt.cookie.same-site}")
    private String sameSite;

    public void saveOrUpdateRefreshToken(User user, String refreshToken) {
        Optional<Token> optionalToken = tokenRepository.findByUserId(user.getId());

        Instant expireDate = Instant.now().plusMillis(refreshTokenExpMillis);

        Token token = optionalToken.map(existingToken ->
                existingToken.updateRefreshToken(
                        refreshToken,
                        LocalDateTime.ofInstant(expireDate, ZoneId.systemDefault())
                )
        ).orElseGet(() ->
                Token.builder()
                        .user(user)
                        .refreshToken(refreshToken)
                        .expireDate(LocalDateTime.ofInstant(expireDate, ZoneId.systemDefault()))
                        .build()
        );
        tokenRepository.save(token);
    }

    // Access 토큰 쿠키 생성 및 응답에 추가
    public void addAccessTokenCookie(HttpServletResponse response, String userId, UserRole role) {
        String token = jwtTokenProvider.generateToken(userId, role);
        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(jwtTokenProvider.getAccessTokenExp() / 1000)
                .sameSite(sameSite)
                .domain(cookieDomain.isBlank() ? null : cookieDomain)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        log.info("AccessToken 쿠키 설정 완료");
    }

    // Refresh 토큰 생성 + 저장 + 쿠키 설정
    public void addRefreshTokenCookie(User user) {
        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()), user.getUserRole());
        saveOrUpdateRefreshToken(user, token);
        log.info("RefreshToken DB 저장");
    }

    // 쿠키 삭제 메서드도 필요하다면 아래 추가
    public void deleteAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite)
                .domain(cookieDomain.isBlank() ? null : cookieDomain)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite)
                .domain(cookieDomain.isBlank() ? null : cookieDomain)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
