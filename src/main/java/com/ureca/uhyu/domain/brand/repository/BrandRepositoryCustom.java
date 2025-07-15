package com.ureca.uhyu.domain.brand.repository;

import com.ureca.uhyu.domain.brand.entity.Brand;

import java.util.List;

public interface BrandRepositoryCustom {
    public List<Brand> findByCategoryOrNameOrTypes(String category,
                                                   String brandName,
                                                   List<String> storeType,
                                                   List<String> benefitType,
                                                   int page,
                                                   int size);
}
