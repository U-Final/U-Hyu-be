package com.ureca.uhyu.domain.brand.controller;

import com.ureca.uhyu.domain.brand.dto.request.CreateBrandReq;
import com.ureca.uhyu.domain.brand.dto.response.BrandInfoRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.dto.response.CreateBrandRes;
import com.ureca.uhyu.domain.brand.service.BrandService;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/brand-list")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "제휴처 목록 조회", description = "제휴처 목록 조회, 필터링 적용 가능")
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

    @Operation(summary = "제휴처 상세 조회", description = "제휴처의 상세 정보 조회 기능")
    @GetMapping("/brand-list/{brand_id}")
    public CommonResponse<BrandInfoRes> getBrandInfo(@PathVariable(name = "brand_id") Long brandId){
        return CommonResponse.success(brandService.findBrandInfo(brandId));
    }

    @Operation(summary = "관리자 제휴 브랜드 추가", description = "관리자 유저 제휴 브랜드 추가 기능")
    @PostMapping("/admin/brand")
    public CommonResponse<CreateBrandRes> createBrand(@RequestBody CreateBrandReq createBrandReq) {
        return CommonResponse.success(brandService.createBrand(createBrandReq));
    }

    @Operation(summary = "관리자 제휴 브랜드 수정", description = "관리자 유저 제휴 브랜드 수정 기능")
    @PutMapping("/admin/brands/{brand_id}")
    public CommonResponse<UpdateBrandRes> updateBrand(@Valid @RequestBody UpdateBrandReq updateBrandReq) {
        return CommonResponse.success(brandService.updateBrand(updateBrandReq));
    }

    @Operation(summary = "관리자 제휴 브랜드 삭제", description = "관리자 유저 제휴 브랜드 삭제 기능")
    @DeleteMapping("/admin/brands/{brand_id}")
    public CommonResponse<ResultCode> deleteBrand(@PathVariable(name = "brand_id") Long brandId) {

    }
}
