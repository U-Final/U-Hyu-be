package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.dto.response.UpdateUserRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.service.UserService;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "개인정보 조회", description = "개인정보 조회: 로그인 필요")
    @GetMapping
    public CommonResponse<GetUserInfoRes> getByUser( User user) {
        return CommonResponse.success(userService.findUserInfo(user));
    }

    @Operation(summary = "개인정보 수정", description = "개인정보 수정: 수정 요청한 값들만 수정")
    @PatchMapping
    public CommonResponse<UpdateUserRes> updateByUser(
             User user, @RequestBody UpdateUserReq request) {
        return CommonResponse.success(userService.updateUserInfo(user, request));
    }
}
