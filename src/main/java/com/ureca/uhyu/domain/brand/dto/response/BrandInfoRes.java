package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "제휴처 상세 조회 응답")
public record BrandInfoRes (
        Long brandId,
        String brandName,
        String logoImage,
        String usageMethod,
        String usageLimit,
        List<BenefitRes> benefitRes
){
    public static BrandInfoRes from(Brand brand){
        List<Benefit> benefits = brand.getBenefits();
        List<BenefitRes> benefitRes = benefits.stream()
                .map(BenefitRes::from)
                .toList();

        return new BrandInfoRes(
                brand.getId(),
                brand.getBrandName(),
                brand.getLogoImage(),
                brand.getUsageMethod(),
                brand.getUsageMethod(),
                benefitRes
        );
    }
}
