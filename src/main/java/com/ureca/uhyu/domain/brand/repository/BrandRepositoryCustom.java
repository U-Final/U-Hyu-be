package com.ureca.uhyu.domain.brand.repository;

import com.ureca.uhyu.domain.brand.dto.response.BrandPageResult;

import java.util.List;

public interface BrandRepositoryCustom {
    public BrandPageResult findByCategoryOrNameOrTypes(String category,
                                                       List<String> storeType,
                                                       List<String> benefitType,
                                                       String brandName,
                                                       int page,
                                                       int size);
}
