package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.brand.dto.response.*;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.brand.repository.CategoryRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<BrandNameRes> findBrandByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_CATEGORY));
        List<Brand> brands = brandRepository.findByCategory(category);

        return brands.stream()
                .map(BrandNameRes::from)
                .toList();
    }
}
