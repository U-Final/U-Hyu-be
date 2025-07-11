package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingRequest;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.service.UserService;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public CommonResponse<ResultCode> onboarding(
            @Valid @RequestBody UserOnboardingRequest request, HttpServletResponse response,
            @CurrentUser User user
    ) {

        Long userId = userService.saveOnboardingInfo(request, user);

        Cookie accessCookie = tokenService.createAccessTokenCookie(String.valueOf(userId), UserRole.USER);
        tokenService.createRefreshToken(user);

        response.addCookie(accessCookie);

        return CommonResponse.success(ResultCode.USER_ONBOARDING_SUCCESS);
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