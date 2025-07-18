package com.ureca.uhyu.domain.auth.service;

import com.ureca.uhyu.domain.auth.entity.Token;
import com.ureca.uhyu.domain.auth.jwt.JwtTokenProvider;
import com.ureca.uhyu.domain.auth.repository.TokenRepository;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    // Access 토큰 쿠키 생성
    public Cookie createAccessTokenCookie(String userId, UserRole role) {
        log.info("access 토큰 쿠키 생성");
        String accessToken = jwtTokenProvider.generateToken(userId, role);
        return buildHttpOnlyCookie("access_token", accessToken, jwtTokenProvider.getAccessTokenExp());
    }

    // Refresh 토큰 쿠키 생성 및 DB 저장
    public void createRefreshToken(User user) {
        String refreshToken = jwtTokenProvider.generateToken(
                String.valueOf(user.getId()), user.getUserRole());
        log.info("refresh 토큰 저장");
        saveOrUpdateRefreshToken(user, refreshToken);
    }

    private Cookie buildHttpOnlyCookie(String name, String token, long maxAgeMillis) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (maxAgeMillis / 1000)); // Convert milliseconds to seconds
        return cookie;
    }
}