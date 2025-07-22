package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.brand.dto.request.CreateBrandReq;
import com.ureca.uhyu.domain.brand.dto.request.UpdateBrandReq;
import com.ureca.uhyu.domain.brand.dto.response.*;
import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.brand.repository.CategoryRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    public BrandListRes findBrands(String category, List<String> storeType, List<String> benefitType,
                                   String brandName, int page, int size) {
        BrandPageResult result = brandRepository.findByCategoryOrNameOrTypes(
                category, storeType, benefitType, brandName, page, size
        );

        List<BrandRes> brandList = result.brandList().stream()
                .map(BrandRes::from)
                .toList();

        long totalCount = result.totalCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);
        boolean hasNext = page + 1 < totalPages;

        return BrandListRes.from(brandList, hasNext, totalPages, page);
    }

    public BrandInfoRes findBrandInfo(Long brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new GlobalException(ResultCode.BRAND_NOT_FOUND));
        return BrandInfoRes.from(brand);
    }

    @Transactional
    public CreateUpdateBrandRes createBrand(CreateBrandReq request) {

        if (brandRepository.existsByBrandName(request.brandName())) {
            throw new GlobalException(ResultCode.BRAND_NAME_DUPLICATED);
        }

        try {
            StoreType.valueOf(request.store_type().name());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(ResultCode.INVALID_STORE_TYPE);
        }

        // 카테고리 조회
        Category category = categoryRepository.findById(request.category_id())
                .orElseThrow(() -> new GlobalException(ResultCode.CATEGORY_NOT_FOUND));

        Brand brand = Brand.builder()
                .brandName(request.brandName())
                .logoImage(request.brandImg())
                .usageMethod(request.usage_method())
                .usageLimit(request.usage_limit())
                .storeType(request.store_type())
                .category(category)
//                .stores() // 매장 정보는 없음
                .build();

        List<Benefit> benefits = request.data().stream()
                .map(dto -> Benefit.builder()
                        .brand(brand)
                        .grade(dto.grade())
                        .description(dto.description())
                        .benefitType(dto.benefitType())
                        .build())
                .toList();

        brand.setBenefits(benefits); // brand와 benefit 연관관계 매핑
        brandRepository.save(brand);

        return new CreateUpdateBrandRes(brand.getId());
    }

    @Transactional
    public CreateUpdateBrandRes updateBrand(UpdateBrandReq request) {
        // 1. 브랜드 조회
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new GlobalException(ResultCode.BRAND_NOT_FOUND));

        // 브랜드명 중복 검사
        if (brandRepository.existsByBrandName(request.brandName())) {
            throw new GlobalException(ResultCode.BRAND_NAME_DUPLICATED);
        }

        // storeType enum 검사
        try {
            StoreType.valueOf(request.store_type().name());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(ResultCode.INVALID_STORE_TYPE);
        }

        // 2. 카테고리 조회
        Category category = categoryRepository.findById(request.category_id())
                .orElseThrow(() -> new GlobalException(ResultCode.CATEGORY_NOT_FOUND));
        brand.changeCategory(category);

        // 3. 필드 업데이트
        brand.updateBrandInfo(
                request.brandName(),
                request.brandImg(),
                request.usage_method(),
                request.usage_limit(),
                request.store_type()
        );

        List<Benefit> updateBenefits = request.data().stream()
                .map(dto -> Benefit.builder()
                        .brand(brand)
                        .grade(dto.grade())
                        .description(dto.description())
                        .benefitType(dto.benefitType())
                        .build())
                .toList();

        brand.setBenefits(updateBenefits);

        return new CreateUpdateBrandRes(brand.getId());
    }

    @Transactional
    public void deleteBrand(Long brandId) {
        Brand brand = brandRepository.findByIdAndDeletedFalse(brandId)
                .orElseThrow(() -> new GlobalException(ResultCode.BRAND_NOT_FOUND));
        brand.markDeleted();
    }
}
