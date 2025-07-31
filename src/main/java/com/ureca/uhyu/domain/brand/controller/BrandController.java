package com.ureca.uhyu.domain.brand.controller;

import com.ureca.uhyu.domain.brand.dto.response.InterestBrandRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandInfoRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.service.BrandService;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "브랜드/제휴점", description = "제휴점 목록 조회 및 상세 정보 관련 API")
@RestController
@RequiredArgsConstructor
public class BrandController implements BrandControllerDocs {

    private final BrandService brandService;

    @Override
    @GetMapping("/brand-list")
    public CommonResponse<BrandListRes> getBrands(
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
    ){
        return CommonResponse.success(
                brandService.findBrands(category, storeType, benefitType, brandName, page, size)
        );
    }

    @Override
    @GetMapping("/brand-list/{brand_id}")
    public CommonResponse<BrandInfoRes> getBrandInfo(
            @Parameter(
                    description = "제휴처 ID",
                    example = "1"
            ) @PathVariable(name = "brand_id") Long brandId){
        return CommonResponse.success(brandService.findBrandInfo(brandId));
    }

    @Override
    @GetMapping("/brand-list/interest")
    public CommonResponse<List<InterestBrandRes>> getInterestBrandList(){
        return CommonResponse.success(brandService.findInterestBrandList());
    }
}
