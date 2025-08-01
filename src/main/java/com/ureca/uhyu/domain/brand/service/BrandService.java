package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.admin.dto.response.AdminBrandListRes;
import com.ureca.uhyu.domain.admin.dto.response.AdminBrandRes;
import com.ureca.uhyu.domain.brand.dto.BrandPageResult;
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

    public AdminBrandListRes findBrandsForAdmin(String category, List<String> storeType, List<String> benefitType,
                                                String brandName, int page, int size) {
        BrandPageResult result = brandRepository.findByCategoryOrNameOrTypes(
                category, storeType, benefitType, brandName, page, size
        );

        List<AdminBrandRes> brandList = result.brandList().stream()
                .map(AdminBrandRes::from)
                .toList();

        long totalCount = result.totalCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);
        boolean hasNext = page + 1 < totalPages;

        return AdminBrandListRes.from(brandList, hasNext, totalPages, page);
    }

    public BrandInfoRes findBrandInfo(Long brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new GlobalException(ResultCode.BRAND_NOT_FOUND));
        return BrandInfoRes.from(brand);
    }

    public List<BrandNameRes> findBrandByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_CATEGORY));
        List<Brand> brands = brandRepository.findByCategory(category);

        return brands.stream()
                .map(BrandNameRes::from)
                .toList();
    }

    @Transactional
    public CreateUpdateBrandRes createBrand(CreateBrandReq request) {

        if (brandRepository.existsByBrandName(request.brandName())) {
            throw new GlobalException(ResultCode.BRAND_NAME_DUPLICATED);
        }

        try {
            StoreType.valueOf(request.storeType().name());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(ResultCode.INVALID_STORE_TYPE);
        }

        // 카테고리 조회
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new GlobalException(ResultCode.CATEGORY_NOT_FOUND));

        Brand brand = Brand.builder()
                .brandName(request.brandName())
                .logoImage(request.brandImg())
                .usageMethod(request.usageMethod())
                .usageLimit(request.usageLimit())
                .storeType(request.storeType())
                .category(category)
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
    public CreateUpdateBrandRes updateBrand(Long brandId, UpdateBrandReq request) {

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new GlobalException(ResultCode.BRAND_NOT_FOUND));

        if (brandRepository.existsByBrandName(request.brandName())) {
            throw new GlobalException(ResultCode.BRAND_NAME_DUPLICATED);
        }

        try {
            StoreType.valueOf(request.storeType().name());
        } catch (IllegalArgumentException e) {
            throw new GlobalException(ResultCode.INVALID_STORE_TYPE);
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new GlobalException(ResultCode.CATEGORY_NOT_FOUND));
        brand.changeCategory(category);

        brand.updateBrandInfo(
                request.brandName(),
                request.brandImg(),
                request.usageMethod(),
                request.usageLimit(),
                request.storeType()
        );

        List<Benefit> updateBenefits = request.data().stream()
                .map(dto -> Benefit.builder()
                        .brand(brand)
                        .grade(dto.grade())
                        .description(dto.description())
                        .benefitType(dto.benefitType())
                        .build())
                .toList();

//        brand.setBenefits(updateBenefits);
        brand.getBenefits().clear();
        brand.getBenefits().addAll(updateBenefits);

        return new CreateUpdateBrandRes(brand.getId());
    }

    @Transactional
    public void deleteBrand(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new GlobalException(ResultCode.BRAND_NOT_FOUND));
        brandRepository.delete(brand);
    }

    public List<InterestBrandRes> findInterestBrandList() {
        // 각 카테고리를 대표하는 브랜드들의 id 입력, 베포 db 기준이라 로컬에서는 에러가 날 수도
        List<Long> recommendIdList = List.of(95L, 2L, 10L, 15L, 25L, 30L, 35L, 39L, 55L, 71L, 83L, 101L, 110L, 118L);
        List<Brand> brandList = brandRepository.findByIdIn(recommendIdList);
        return brandList.stream().map(InterestBrandRes::from).toList();
    }
}