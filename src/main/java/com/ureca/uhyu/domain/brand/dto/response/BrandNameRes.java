package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리별 제휴처 목록 조회 응답 dto")
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
