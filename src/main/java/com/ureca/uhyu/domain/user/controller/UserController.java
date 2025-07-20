package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.auth.dto.UserEmailCheckRequest;
import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingRequest;
import com.ureca.uhyu.domain.user.dto.response.BookmarkRes;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.dto.response.UpdateUserRes;
import com.ureca.uhyu.domain.user.dto.response.UserStatisticsRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.service.UserService;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
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
            @Valid @RequestBody UserOnboardingRequest request,
            HttpServletResponse response,
            @CurrentUser User user
    ) {
        Long userId = userService.saveOnboardingInfo(request, user);

        // ✅ access token 쿠키 추가
        tokenService.addAccessTokenCookie(response, String.valueOf(userId), UserRole.USER);

        // ✅ refresh token DB 저장
        tokenService.addRefreshTokenCookie(user);

        return CommonResponse.success(ResultCode.USER_ONBOARDING_SUCCESS, null);
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
    @PostMapping("/check-email")
    public CommonResponse<ResultCode> checkEmailDuplicate(@RequestBody UserEmailCheckRequest request, @CurrentUser User user) {
        userService.validateEmailAvailability(user, request.email());
        return CommonResponse.success(ResultCode.EMAIL_CHECK_SUCCESS, null); // true : 중복됨
    }

    @Operation(summary = "즐겨찾기 조회", description = "즐겨찾기 목록 조회")
    @GetMapping("/bookmark")
    public CommonResponse<List<BookmarkRes>> getBookmarkList(@CurrentUser User user) {
        return CommonResponse.success(userService.findBookmarkList(user));
    }

    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 목록 중 1개 삭제")
    @DeleteMapping("/bookmark/{bookmark_id}")
    public CommonResponse<ResultCode> deleteBookmark(@CurrentUser User user, @PathVariable Long bookmark_id) {
        userService.deleteBookmark(user, bookmark_id);
        return CommonResponse.success(ResultCode.BOOKMARK_DELETE_SUCCESS);
    }

    @Operation(summary = "사용자 활동내역 조회", description = "사용자의 자주 조회한 브랜드, 이번 달 받은 혜택(=사용자 통계) 제공")
    @GetMapping("/statistics")
    public CommonResponse<UserStatisticsRes> getUserStatistics(@CurrentUser User user) {
        return CommonResponse.success(userService.findUserStatistics(user));
    }
}
