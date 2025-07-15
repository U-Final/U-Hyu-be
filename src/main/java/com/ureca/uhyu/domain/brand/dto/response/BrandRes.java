package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;

public record BrandRes (
        Long brandId,
        String brandName,
        String logoImage,
        String description
){
    public static BrandRes from(Brand brand){
        // TODO : 혜택 어떻게 꺼내올지 아직 고민중 (전부 다 가져와야하나?)
        Benefit benefit = brand.getBenefits().get(0);

        return new BrandRes(
                brand.getId(),
                brand.getBrandName(),
                brand.getLogoImage(),
                benefit.getDescription()
        );
    }
}
