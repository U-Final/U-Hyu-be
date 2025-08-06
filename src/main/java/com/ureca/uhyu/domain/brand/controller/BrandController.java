package com.ureca.uhyu.domain.brand.controller;

import com.ureca.uhyu.domain.brand.dto.response.InterestBrandRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandInfoRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.service.BrandService;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Override
    @GetMapping("/brand-list/{brand_id}")
    public CommonResponse<BrandInfoRes> getBrandInfo(
            @PathVariable(name = "brand_id") Long brandId){
        return CommonResponse.success(brandService.findBrandInfo(brandId));
    }

    @Override
    @GetMapping("/brand-list/interest")
    public CommonResponse<List<InterestBrandRes>> getInterestBrandList(){
        return CommonResponse.success(brandService.findInterestBrandList());
    }
}
