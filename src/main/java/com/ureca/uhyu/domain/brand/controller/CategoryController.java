package com.ureca.uhyu.domain.brand.controller;

import com.ureca.uhyu.domain.brand.dto.response.BrandNameRes;
import com.ureca.uhyu.domain.brand.dto.response.CategoryListRes;
import com.ureca.uhyu.domain.brand.service.BrandService;
import com.ureca.uhyu.domain.brand.service.CategoryService;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final BrandService brandService;

    @Operation(summary = "카테고리별 제휴처 목록 조회", description = "카테고리를 요청값으로 받으면 해당 카테고리의 브랜드들 조회하는 기능")
    @GetMapping("/{category_id}")
    public CommonResponse<List<BrandNameRes>> getBrandByCategoryId(@PathVariable(name = "category_id") Long categoryId){
        return CommonResponse.success(brandService.findBrandByCategoryId(categoryId));
    }
}
