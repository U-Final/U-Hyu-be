package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;

public record CreateUpdateBrandRes(
        Long brandId
) {
    public static CreateUpdateBrandRes from(Brand brand) {
        return new CreateUpdateBrandRes(brand.getId());
    }
}
