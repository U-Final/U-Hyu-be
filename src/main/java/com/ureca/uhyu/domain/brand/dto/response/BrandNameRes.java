package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;

public record BrandNameRes (
        Long brandId,
        String brandName
){
    public static BrandNameRes from(Brand brand){
        return new BrandNameRes(
                brand.getId(),
                brand.getBrandName()
        );
    }
}
