package com.ureca.uhyu.domain.user.controller;

import com.nimbusds.oauth2.sdk.TokenResponse;
import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingRequest;
import com.ureca.uhyu.domain.user.dto.response.UserOnboardingResponse;
import com.ureca.uhyu.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    /**
     * 신규 유저 온보딩 정보 저장 + 권한 변경 + 토큰 재발급
     */
    @Operation(summary = "신규 유저 추가 입력 정보 저장", description = "신규 유저 추가 입력 정보 저장, user_role 변경, 토큰 재발급")
    @PostMapping("/extra-info")
    public ResponseEntity<UserOnboardingResponse> onboarding(
            @Valid @RequestBody UserOnboardingRequest request
    ) {
        // 1. 온보딩 정보 저장 및 유저 권한 변경
        Long userId = userService.saveOnboardingInfo(request);

        // 2. access, refresh 토큰 재발급
        TokenResponse tokens = tokenService.createTokenSet(userId);

        // 3. 응답 DTO 구성 - 토큰은 쿠키에
//        UserOnboardingResponse response = new UserOnboardingResponse(
//                userId
//                tokens.accessToken(),
//                tokens.refreshToken()
//        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}