package com.ureca.uhyu.domain.brand.controller;

import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.service.BrandService;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand-list")
@RequiredArgsConstructor
public class BrandController {

    public final BrandService brandService;

    @Operation(summary = "제휴처 목록 조회", description = "제휴처 목록 조회, 필터링 적용 가능")
    @PostMapping
    public CommonResponse<BrandListRes> getBrands(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> tag1,
            @RequestParam(required = false) List<String> tag2,
            @RequestParam(required = false, name = "brand_name") String brandName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return CommonResponse.success(
                brandService.findBrands(category, tag1, tag2, brandName, page, size)
        );
    }
}
