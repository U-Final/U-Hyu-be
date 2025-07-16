package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "제휴처 목록 조회 - 제휴처 1개당 보여줄 정보들")
public record BrandRes (
        Long brandId,
        String brandName,
        String logoImage,
        String description
){
    public static BrandRes from(Brand brand){
        // TODO : 혜택 어떻게 꺼내올지 아직 고민중 (전부 다 가져와야하나?)
        String description = brand.getBenefits().stream()
                .findFirst()
                .map(Benefit::getDescription)
                .orElse(null);

        return new BrandRes(
                brand.getId(),
                brand.getBrandName(),
                brand.getLogoImage(),
                description
        );
    }
}
