package com.ureca.uhyu.domain.brand.controller;

import com.ureca.uhyu.domain.brand.dto.response.BrandInfoRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.dto.response.InterestBrandRes;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BrandControllerDocs {

    @Operation(
            summary = "제휴처 목록 조회",
            description = """
                    제휴처 목록을 조회합니다. 다양한 필터링 옵션을 지원합니다.
                    
                    **필터링 옵션:**
                    - **category**: 카테고리별 필터링
                    - **storeType**: 매장 타입별 필터링 (복수 선택 가능)
                    - **benefitType**: 혜택 타입별 필터링 (복수 선택 가능)
                    - **brand_name**: 브랜드명 검색
                    
                    **카테고리 예시:** "영화/미디어", "쇼핑"
                    **브랜드 예시:** "CGV", "롯데시네마", "메가박스"
                    
                    **페이징:**
                    - **page**: 페이지 번호 (0부터 시작)
                    - **size**: 페이지당 항목 수 (1-100)
                    
                    **인증:** 불필요 (공개 API)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "제휴처 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "제휴처 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "brandList": [
                                                  {
                                                    "brandId": 2,
                                                    "brandName": "CGV",
                                                    "logoImage": "https://example.com/logo.jpg",
                                                    "description": "VVIP : 학생 할인 15%, VIP : 학생 할인 10%, 우수 : 기본 할인 5%"
                                                  }
                                                ],
                                                "hasNext": true,
                                                "totalPages": 5,
                                                "currentPage": 0
                                              }
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
    CommonResponse<BrandListRes> getBrands(
            @Parameter(
                    description = "카테고리 필터",
                    example = "카페"
            ) @RequestParam(required = false) String category,
            @Parameter(
                    description = "매장 타입 필터 (복수 선택 가능)",
                    example = "[\"OFFLINE\", \"ONLINE\"]"
            ) @RequestParam(required = false) List<String> storeType,
            @Parameter(
                    description = "혜택 타입 필터 (복수 선택 가능)",
                    example = "[\"DISCOUNT\", \"GIFT\"]"
            ) @RequestParam(required = false) List<String> benefitType,
            @Parameter(
                    description = "브랜드명 검색",
                    example = "스타벅스"
            ) @RequestParam(required = false, name = "brand_name") String brandName,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            ) @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(
                    description = "페이지당 항목 수 (1-100)",
                    example = "10"
            ) @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    );

    @Operation(
            summary = "제휴처 상세 조회",
            description = "특정 제휴처의 상세 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "제휴처 상세 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "제휴처 상세 정보 조회 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "brandId": 2,
                                                "brandName": "CGV",
                                                "logoImage": "https://example.com/logo.jpg",
                                                "usageMethod": "학생증 제시",
                                                "usageLimit": "월 5회",
                                                "benefitRes": [
                                                  {
                                                    "grade": "VIP",
                                                    "description": "학생 할인 10%"
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
                    description = "제휴처를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "제휴처 없음 예시",
                                    value = """
                                            {
                                              "statusCode": 5001,
                                              "message": "제휴처 정보를 찾을 수 없습니다."
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
    CommonResponse<BrandInfoRes> getBrandInfo(
            @Parameter(
                    description = "제휴처 ID",
                    example = "1"
            ) @PathVariable(name = "brand_id") Long brandId);

    @Operation(summary = "선호 브랜드 목록 조회", description = "온보딩, 개인정보 수정 시 고를 브랜드 목록 조회 (카테고리당 1개)")
    public CommonResponse<List<InterestBrandRes>> getInterestBrandList();

}
