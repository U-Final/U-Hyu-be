package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;

import java.util.List;

public record BrandPageResult(
        List<Brand> brandList,
        long totalCount
) {
}
