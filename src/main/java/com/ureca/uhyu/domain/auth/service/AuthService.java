package com.ureca.uhyu.domain.auth.service;

import com.ureca.uhyu.domain.auth.jwt.JwtTokenProvider;
import com.ureca.uhyu.domain.auth.repository.TokenRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import com.ureca.uhyu.global.util.TokenCookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    public void logout(HttpServletRequest request, HttpServletResponse response, Long userId) {

        String accessToken = TokenCookieUtil.extractAccessToken(request);

        if  (accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
            throw new GlobalException(ResultCode.INVALID_TOKEN);
        }

        // 1. refresh 토큰 삭제
        tokenRepository.deleteByUser_Id(userId);

        // 2. access 토큰 만료
        TokenCookieUtil.expireAccessTokenCookie(response);

        // 3. Spring Security 세션 제거
        SecurityContextHolder.clearContext();
    }
}
