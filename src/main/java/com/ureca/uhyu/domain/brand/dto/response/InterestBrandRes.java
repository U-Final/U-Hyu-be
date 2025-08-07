package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관심 브랜드 목록 조회 응답 dto")
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
