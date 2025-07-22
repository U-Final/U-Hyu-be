package com.ureca.uhyu.domain.brand.controller;

import com.ureca.uhyu.domain.brand.dto.response.BrandInfoRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.service.BrandService;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand-list")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "제휴처 목록 조회", description = "제휴처 목록 조회, 필터링 적용 가능")
    @GetMapping
    public CommonResponse<BrandListRes> getBrands(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> storeType,
            @RequestParam(required = false) List<String> benefitType,
            @RequestParam(required = false, name = "brand_name") String brandName,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ){
        return CommonResponse.success(
                brandService.findBrands(category, storeType, benefitType, brandName, page, size)
        );
    }

    @Operation(summary = "제휴처 상세 조회", description = "제휴처의 상세 정보 조회 기능")
    @GetMapping("/{brand_id}")
    public CommonResponse<BrandInfoRes> getBrandInfo(@PathVariable(name = "brand_id") Long brandId){
        return CommonResponse.success(brandService.findBrandInfo(brandId));
    }


}
