package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;

public record CreateBrandRes(
        Long brandId
) {
    public static CreateBrandRes from(Brand brand) {
        return new CreateBrandRes(brand.getId());
    }
}
