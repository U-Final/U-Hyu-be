package com.ureca.uhyu.domain.auth.controller;

import com.nimbusds.oauth2.sdk.TokenResponse;
import com.ureca.uhyu.domain.auth.jwt.JwtTokenProvider;
import com.ureca.uhyu.domain.auth.repository.TokenRepository;
import com.ureca.uhyu.domain.auth.service.AuthService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.service.UserService;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import com.ureca.uhyu.global.util.TokenCookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenRepository tokenRepository;

    @Operation(summary = "로그아웃", description = "로그인한 사용자의 Access Token 만료, Refresh Token 삭제, Security Context 유저 정보 삭제")
    @PostMapping("/logout")
    public CommonResponse<ResultCode> logout(HttpServletRequest request, HttpServletResponse response,
                                             @CurrentUser User user) {

        authService.logout(request, response, user.getId());

        return CommonResponse.success(ResultCode.LOGOUT_SUCCESS);
    }
}
