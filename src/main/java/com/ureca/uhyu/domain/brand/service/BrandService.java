package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandPageResult;
import com.ureca.uhyu.domain.brand.dto.response.BrandRes;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.user.dto.response.BookmarkRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandListRes findBrands(String category, List<String> storeType, List<String> benefitType,
                                   String brandName, int page, int size) {
        BrandPageResult result = brandRepository.findByCategoryOrNameOrTypes(
                category, brandName, storeType, benefitType, page, size
        );

        List<BrandRes> brandList = result.brandList().stream()
                .map(BrandRes::from)
                .toList();

        long totalCount = result.totalCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);
        boolean hasNext = page + 1 < totalPages;

        return BrandListRes.from(brandList, hasNext, totalPages, page);
    }
}
