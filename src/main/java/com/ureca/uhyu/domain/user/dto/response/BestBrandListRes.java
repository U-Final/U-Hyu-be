package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마커 클릭이 가장 많은 브랜드 top3 응답")
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
