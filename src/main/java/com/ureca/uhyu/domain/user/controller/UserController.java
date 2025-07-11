package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingRequest;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.response.BookmarkRes;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.dto.response.UpdateUserRes;
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

import java.util.List;

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
    @GetMapping
    public CommonResponse<GetUserInfoRes> getByUser(@CurrentUser User user) {
        return CommonResponse.success(userService.findUserInfo(user));
    }

    @Operation(summary = "개인정보 수정", description = "개인정보 수정: 수정 요청한 값들만 수정")
    @PatchMapping
    public CommonResponse<UpdateUserRes> updateByUser(
             @CurrentUser User user,
             @RequestBody UpdateUserReq request
    ) {
        return CommonResponse.success(userService.updateUserInfo(user, request));
    }

    @Operation(summary = "이메일 중복 확인", description = "신규 유저 이메일 입력 중복 확인")
    @GetMapping("/check-email")
    public CommonResponse<ResultCode> checkEmailDuplicate(@RequestParam String email) {
        userService.validateEmailAvailability(email);
        return CommonResponse.success(ResultCode.EMAIL_CHECK_SUCCESS); // true : 중복됨
    }

    @Operation(summary = "즐겨찾기 조회", description = "즐겨찾기 목록 조회")
    @GetMapping("/bookmark")
    public CommonResponse<List<BookmarkRes>> getBookmarkList(@CurrentUser User user) {
        return CommonResponse.success(userService.findBookmarkList(user));
    }
}
