package com.ureca.uhyu.domain.brand.repository;

import com.ureca.uhyu.domain.brand.dto.response.BrandPageResult;

import java.util.List;

public interface BrandRepositoryCustom {
    public BrandPageResult findByCategoryOrNameOrTypes(String category,
                                                       String brandName,
                                                       List<String> storeType,
                                                       List<String> benefitType,
                                                       int page,
                                                       int size);
}
