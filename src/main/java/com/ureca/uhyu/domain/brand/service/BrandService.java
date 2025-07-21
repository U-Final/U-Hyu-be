package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.brand.dto.request.CreateBrandReq;
import com.ureca.uhyu.domain.brand.dto.response.*;
import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
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
    public CreateBrandRes createBrand(CreateBrandReq createBrandReq) {
        // 카테고리 조회
        Category category = categoryRepository.findById(createBrandReq.category_id())
                .orElseThrow(() -> new GlobalException(ResultCode.CATEGORY_NOT_FOUND));

        Brand brand = Brand.builder()
                .brandName(createBrandReq.brandName())
                .logoImage(createBrandReq.brandImg())
                .usageMethod(createBrandReq.usage_method())
                .usageLimit(createBrandReq.usage_limit())
                .storeType(createBrandReq.store_type())
//                .stores() // 매장 정보는 없음
                .build();

        List<Benefit> benefits = createBrandReq.data().stream()
                .map(dto -> Benefit.builder()
                        .brand(brand)
                        .grade(dto.grade())
                        .description(dto.description())
                        .benefitType(dto.benefitType())
                        .build())
                .toList();

        brand.setBenefits(benefits); // brand와 benefit 연관관계 매핑
        brandRepository.save(brand);

        return new CreateBrandRes(brand.getId());
    }
}
