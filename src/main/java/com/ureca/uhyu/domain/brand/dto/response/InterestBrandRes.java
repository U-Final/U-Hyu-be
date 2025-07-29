package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;

public record InterestBrandRes (
        Long brandId,
        String brandName,
        String logoImage
){
    public static InterestBrandRes from(Brand brand){
        return new InterestBrandRes(
                brand.getId(),
                brand.getBrandName(),
                brand.getLogoImage()
        );
    }
}
