package com.ureca.uhyu.domain.auth.controller;

import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthControllerDocs {
    @Operation(
            summary = "로그아웃",
            description = """
                    로그인한 사용자의 로그아웃을 처리합니다.
                    
                    **처리 과정:**
                    1. Access Token 만료 처리
                    2. Refresh Token 삭제
                    3. Security Context의 유저 정보 삭제
                    4. 쿠키에서 토큰 정보 제거
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "로그아웃 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 2005,
                                              "message": "로그아웃에 성공했습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (토큰이 없거나 유효하지 않음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                                            {
                                              "statusCode": 1006,
                                              "message": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            )
    })
    public CommonResponse<ResultCode> logout(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response,
            @Parameter(
                    description = "현재 로그인된 사용자 정보 (JWT 토큰에서 추출)",
                    hidden = true
            ) @CurrentUser User user
    );
}
