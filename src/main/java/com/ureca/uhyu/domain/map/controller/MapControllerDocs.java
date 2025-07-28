package com.ureca.uhyu.domain.map.controller;

import com.ureca.uhyu.domain.map.dto.response.MapBookmarkRes;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MapControllerDocs {

    @Operation(
            summary = "지도 매장 검색",
            description = """
                    지도에서 매장을 검색하고 필터링합니다.
                    
                    **검색 조건:**
                    - **lat, lon**: 중심 좌표 (위도, 경도)
                    - **radius**: 검색 반경 (미터 단위)
                    - **category**: 카테고리별 필터링 (선택) - 부분 문자열 검색
                    - **brand**: 브랜드명 필터링 (선택) - 부분 문자열 검색
                    
                    **카테고리 예시:** "영화/미디어", "쇼핑"
                    **브랜드 예시:** "CGV", "롯데시네마", "메가박스"
                    
                    **인증:** 불필요 (공개 API)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "매장 검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "매장 검색 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": [
                                                {
                                                  "storeId": 1,
                                                  "storeName": "CGV 동대문",
                                                  "categoryName": "영화/미디어",
                                                  "addressDetail": "서울특별시 중구 장충단로13길 20",
                                                  "benefit": null,
                                                  "logo_image": "https://example.com/logo.jpg",
                                                  "brandName": "CGV",
                                                  "latitude": 37.5687346,
                                                  "longitude": 127.0076665
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @GetMapping("/stores")
    public CommonResponse<List<MapRes>> getFilteredStores(
            @Parameter(
                    description = "중심 위도",
                    example = "36.1234"
            ) @RequestParam Double lat,
            @Parameter(
                    description = "중심 경도",
                    example = "129.5678"
            ) @RequestParam Double lon,
            @Parameter(
                    description = "검색 반경 (미터)",
                    example = "1000"
            ) @RequestParam Double radius,
            @Parameter(
                    description = "카테고리 필터 (부분 문자열 검색)",
                    example = "카페"
            ) @RequestParam(required = false) String category,
            @Parameter(
                    description = "브랜드명 필터 (부분 문자열 검색)",
                    example = "스타벅스"
            ) @RequestParam(required = false) String brand
    );

    @Operation(
            summary = "매장 상세정보 조회",
            description = """
                    지도 마커를 클릭했을 때 매장의 상세 정보를 조회합니다.
                    
                    **포함 정보:**
                    - 등급별 혜택 정보
                    - 제공 횟수
                    - 이용 방법
                    - 즐겨찾기 상태
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "매장 상세정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "매장 상세정보 조회 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "storeName": "CGV 동대문",
                                                "isFavorite": true,
                                                "favoriteCount": 5,
                                                "benefits": {
                                                  "grade": "VIP",
                                                  "benefitText": "학생 할인 10%"
                                                },
                                                "usageLimit": "월 5회",
                                                "usageMethod": "학생증 제시"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매장을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
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
    @GetMapping("/detail/stores/{storeId}")
    public CommonResponse<StoreDetailRes> getStoreDetail(
            @Parameter(
                    description = "매장 ID",
                    example = "1"
            ) @PathVariable Long storeId,
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user
    );

    @Operation(
            summary = "매장 즐겨찾기 토글",
            description = """
                    매장 상세정보에서 즐겨찾기 상태를 토글합니다.
                    
                    **동작:**
                    - 즐겨찾기가 되어있지 않으면 추가
                    - 즐겨찾기가 되어있으면 삭제
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "즐겨찾기 토글 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "즐겨찾기 토글 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "storeId": 1,
                                                "isBookmarked": true
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "매장을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
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
    @PostMapping("/{storeId}")
    public CommonResponse<MapBookmarkRes> toggleBookmark(
            @Parameter(
                    description = "매장 ID",
                    example = "1"
            ) @PathVariable Long storeId,
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user
    );

    @Operation(summary = "추천 매장 조회", description = "사용자의 추천 브랜드에 해당하는 근처 매장 조회")
    @GetMapping("/recommendation/stores")
    public CommonResponse<List<MapRes>> getRecommendedStores(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radius,
            @CurrentUser User user
    );
}
