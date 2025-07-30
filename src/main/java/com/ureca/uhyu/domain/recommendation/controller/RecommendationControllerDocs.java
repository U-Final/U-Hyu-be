package com.ureca.uhyu.domain.recommendation.controller;

import com.ureca.uhyu.domain.recommendation.dto.response.RecommendationRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

public interface RecommendationControllerDocs {

    @Operation(
            summary = "사용자 맞춤 추천 조회",
            description = """
                    사용자의 활동 패턴을 기반으로 맞춤형 브랜드를 추천합니다.
                    
                    **추천 기준:**
                    - 최근 이용한 브랜드
                    - 관심 있는 브랜드
                    - 사용자 등급별 혜택
                    - 지리적 위치
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "추천 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": [
                                                {
                                                  "brandId": 2,
                                                  "brandName": "CGV",
                                                  "rank": 1
                                                },
                                                {
                                                  "brandId": 3,
                                                  "brandName": "롯데시네마",
                                                  "rank": 2
                                                },
                                                {
                                                  "brandId": 4,
                                                  "brandName": "메가박스",
                                                  "rank": 3
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    CommonResponse<List<RecommendationRes>> recommendation(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user);
}
