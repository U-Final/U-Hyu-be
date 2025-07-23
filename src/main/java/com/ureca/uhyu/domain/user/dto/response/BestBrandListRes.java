package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;

public record BestBrandListRes(
        Long bestBrandId,
        String bestBrandName,
        String bestBrandImage
) {
    public static BestBrandListRes from(Brand brand) {
        return new BestBrandListRes(
                brand.getId(),
                brand.getBrandName(),
                brand.getLogoImage()
        );
    }
}
