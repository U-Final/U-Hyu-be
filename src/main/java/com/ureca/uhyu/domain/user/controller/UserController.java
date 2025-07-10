package com.ureca.uhyu.domain.user.controller;

import com.nimbusds.oauth2.sdk.TokenResponse;
import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingRequest;
import com.ureca.uhyu.domain.user.dto.response.UserOnboardingResponse;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.domain.user.service.UserService;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
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
    public CommonResponse<UserOnboardingResponse> onboarding( // 응답 객체에 UserOnboardingResponse dto 객체를 body에 담아 리턴
            @Valid @RequestBody UserOnboardingRequest request, HttpServletResponse response
    ) {
        // 1. 온보딩 정보 저장 및 유저 권한 변경
        Long userId = userService.saveOnboardingInfo(request);
        User user = userService.getUserById(userId);

        // 2. access, refresh 토큰 재발급
        Cookie accessCookie = tokenService.createAccessTokenCookie(String.valueOf(userId), UserRole.USER);
        tokenService.createRefreshToken(user);

        response.addCookie(accessCookie);

        return CommonResponse.success(null); // 보내줄 데이터 없음 - 성공했다는 응답만
    }

    @Operation(summary = "개인정보 조회", description = "개인정보 조회: 로그인 필요")
    @GetMapping("{userId}")
    public CommonResponse<GetUserInfoRes> getByUser(@PathVariable String userId) {
        //스웨거 상에서 api 동작 테스트를 해야하는 경우 해당 부분 주석 처리 후 실행할 것
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserId = (String) authentication.getPrincipal();
//        String role = authentication.getAuthorities().iterator().next().getAuthority();
//
//        // 인가 로직
//        if (!currentUserId.equals(userId)) {
//            return CommonResponse.fail(ResultCode.FORBIDDEN, "본인의 정보만 조회할 수 있습니다.");
//        }
//
//        if (!role.equals("ROLE_USER")) {
//            return CommonResponse.fail(ResultCode.FORBIDDEN, "일반 사용자만 접근 가능합니다.");
//        }

        // 로직 진행
        return CommonResponse.success(userService.findUserInfo(Long.parseLong(userId)));
    }
}