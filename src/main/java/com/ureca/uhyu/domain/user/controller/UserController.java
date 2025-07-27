package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.auth.dto.UserEmailCheckRequest;
import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingReq;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사용자", description = "사용자 정보 관리 및 온보딩 관련 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/extra-info")
    public CommonResponse<ResultCode> onboarding(
            @Valid @RequestBody UserOnboardingReq request,
            HttpServletResponse response,
            @CurrentUser User user
    ) {
        Long userId = userService.saveOnboardingInfo(request, user);

        tokenService.addAccessTokenCookie(response, String.valueOf(userId), UserRole.USER);
        tokenService.saveRefreshToken(user);

        return CommonResponse.success(ResultCode.USER_ONBOARDING_SUCCESS, null);
    }


    @GetMapping
    public CommonResponse<GetUserInfoRes> getByUser(@CurrentUser User user) {
        return CommonResponse.success(userService.findUserInfo(user));
    }


    @PatchMapping
    public CommonResponse<UpdateUserRes> updateByUser(
            @CurrentUser User user,
            @RequestBody UpdateUserReq request
    ) {
        return CommonResponse.success(userService.updateUserInfo(user, request));
    }


    @PostMapping("/check-email")
    public CommonResponse<ResultCode> checkEmailDuplicate(
            @RequestBody UserEmailCheckRequest request,
            @CurrentUser User user
    ) {
        userService.validateEmailAvailability(user, request.email());
        return CommonResponse.success(ResultCode.EMAIL_CHECK_SUCCESS, null);
    }


    @GetMapping("/bookmark")
    public CommonResponse<List<BookmarkRes>> getBookmarkList(
            @CurrentUser User user
    ) {
        return CommonResponse.success(userService.findBookmarkList(user));
    }


    @DeleteMapping("/bookmark/{bookmark_id}")
    public CommonResponse<ResultCode> deleteBookmark(
            @CurrentUser User user,
            @PathVariable Long bookmark_id
    ) {
        userService.deleteBookmark(user, bookmark_id);
        return CommonResponse.success(ResultCode.BOOKMARK_DELETE_SUCCESS);
    }


    @GetMapping("/statistics")
    public CommonResponse<UserStatisticsRes> getUserStatistics(
            @CurrentUser User user
    ) {
        return CommonResponse.success(userService.findUserStatistics(user));
    }

    @PostMapping("/action-logs")
    public CommonResponse<> actionLogs()
}
