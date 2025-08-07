package com.ureca.uhyu.domain.auth.controller;

import com.ureca.uhyu.domain.auth.service.AuthService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증/인가", description = "사용자 인증 및 권한 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs{

    private final AuthService authService;

    @PostMapping("/logout")
    public CommonResponse<ResultCode> logout(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response,
            @Parameter(
                    description = "현재 로그인된 사용자 정보 (JWT 토큰에서 추출)",
                    hidden = true
            ) @CurrentUser User user) {

        authService.logout(request, response, user.getId());

        return CommonResponse.success(ResultCode.LOGOUT_SUCCESS);
    }
}
