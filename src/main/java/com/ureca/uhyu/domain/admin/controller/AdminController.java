package com.ureca.uhyu.domain.admin.controller;

import com.ureca.uhyu.domain.admin.dto.response.BookmarksByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsFilterByCategoryRes;
import com.ureca.uhyu.domain.admin.service.AdminService;
import com.ureca.uhyu.domain.brand.dto.request.CreateBrandReq;
import com.ureca.uhyu.domain.brand.dto.request.UpdateBrandReq;
import com.ureca.uhyu.domain.brand.dto.response.CategoryListRes;
import com.ureca.uhyu.domain.brand.dto.response.CreateUpdateBrandRes;
import com.ureca.uhyu.domain.brand.service.BrandService;
import com.ureca.uhyu.domain.brand.service.CategoryService;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "관리자", description = "관리자 기능  관련 API")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BrandService brandService;
    private final CategoryService categoryService;
    private final AdminService adminService;

    @Operation(summary = "관리자 제휴 브랜드 추가", description = "관리자 유저 제휴 브랜드 추가 기능")
    @PostMapping("/brand")
    public CommonResponse<CreateUpdateBrandRes> createBrand(@Valid @RequestBody CreateBrandReq createBrandReq) {
        return CommonResponse.success(brandService.createBrand(createBrandReq));
    }

    @Operation(summary = "관리자 제휴 브랜드 수정", description = "관리자 유저 제휴 브랜드 수정 기능")
    @PutMapping("/brands/{brand_id}")
    public CommonResponse<CreateUpdateBrandRes> updateBrand(
            @PathVariable(name = "brand_id") Long brandId,
            @Valid @RequestBody UpdateBrandReq updateBrandReq) {
        return CommonResponse.success(brandService.updateBrand(brandId, updateBrandReq));
    }

    @Operation(summary = "관리자 제휴 브랜드 삭제", description = "관리자 유저 제휴 브랜드 삭제 기능")
    @DeleteMapping("/brands/{brand_id}")
    public CommonResponse<ResultCode> deleteBrand(@Valid @PathVariable(name = "brand_id") Long brandId) {
        brandService.deleteBrand(brandId);
        return CommonResponse.success(ResultCode.DELETE_BRAND_SUCCESS);
    }

    @Operation(summary = "카테고리 목록 조회", description = "관리자 제휴 브랜드 추가, 수정 시 카테고리 항목 조회 요청 api")
    @GetMapping("/categories")
    public CommonResponse<List<CategoryListRes>> getCategories() {
        return CommonResponse.success(categoryService.getAllCategories());
    }

    @Operation(summary = "카테고리, 브랜드별 즐겨찾기 수 통계", description = "관리자가 즐겨찾기 수에 대한 카테고리별, 브랜드 별 통계 확인 가능")
    @GetMapping("/statistics/bookmark")
    public CommonResponse<List<BookmarksByCategoryRes>> getBookmarksByCategoryAndBrand() {
        return CommonResponse.success(adminService.findBookmarksByCategoryAndBrand());
    }

    @Operation(summary = "카테고리별 필터링 수 통계", description = "관리자가 필터링 된 횟수에 대한 카테고리별 통계 확인 가능")
    @GetMapping("/statistics/filter")
    public CommonResponse<List<StatisticsFilterByCategoryRes>> getStatisticsFilterByCategory() {
        return CommonResponse.success(adminService.findStatisticsFilterByCategory());
    }
}
