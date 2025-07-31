package com.ureca.uhyu.domain.guest.controller;

import com.ureca.uhyu.domain.mymap.dto.response.MyMapRes;
import com.ureca.uhyu.domain.guest.dto.response.GuestRecommendationRes;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface GuestControllerDocs {

    @Operation(
            summary = "UUID 기반 My Map 조회(비회원)",
            description = """
                    비회원이 UUID를 통해 공유된 My Map의 상세 정보를 조회합니다.
                 
                   **특징:**
                    - 인증 불필요 (토큰 없이 접근 가능)
                    - 회원용 조회와 동일한 데이터 반환
                    - 공유 목적으로 사용됨
                   
                    **인증 불필요:** 별도 토큰 없이 접근 가능
                   """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "My Map 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "비회원 My Map 조회 성공 예시",
                                    value = """
                                           {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "myMapListId": 1,
                                                "title": "영화관 투어",
                                                "markerColor": "RED",
                                                "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                                "isMine": false,
                                                "stores": [
                                                  {
                                                    "storeId": 1,
                                                    "storeName": "CGV 동대문",
                                                    "address": "서울특별시 중구 장충단로13길 20",
                                                    "latitude": 37.5687346,
                                                    "longitude": 127.0076665
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "My Map을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    CommonResponse<MyMapRes> getMyMapByUUIDWithGuest(
            @Parameter(
                    description = "My Map UUID",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ) @PathVariable String uuid
    );


    @Operation(
            summary = "비로그인 사용자 인기 브랜드 Top3 조회",
            description = """
                    전체 사용자 방문 기록을 기반으로 가장 많이 이용된 브랜드 상위 3개를 반환합니다.
                    
                    **추천 기준:**
                    - 모든 사용자의 History 데이터를 기반으로 방문 수가 많은 브랜드 순서로 정렬
                    - 브랜드명, 로고 이미지 포함
                    
                    **인증 불필요:** 누구나 접근 가능
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "비로그인 추천 브랜드 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommonResponse.class),
                    examples = @ExampleObject(
                            name = "비로그인 인기 브랜드 Top3 예시",
                            value = """
                                    {
                                      "statusCode": 0,
                                      "message": "정상 처리 되었습니다.",
                                      "data": [
                                        {
                                          "brandId": 1,
                                          "brandName": "스타벅스",
                                          "logoImg": "starbucks.png"
                                        },
                                        {
                                          "brandId": 2,
                                          "brandName": "투썸플레이스",
                                          "logoImg": "twosome.png"
                                        },
                                        {
                                          "brandId": 3,
                                          "brandName": "이디야",
                                          "logoImg": "ediya.png"
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    CommonResponse<List<GuestRecommendationRes>> guestTop3Recommendation();
}
