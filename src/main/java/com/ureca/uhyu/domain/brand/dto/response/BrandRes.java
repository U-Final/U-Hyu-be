package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "제휴처 목록 조회 - 제휴처 1개당 보여줄 정보들")
public record BrandRes (
        Long brandId,
        String brandName,
        String logoImage,
        String description
){
    public static BrandRes from(Brand brand){
        List<BenefitRes> benefitRes = brand.getBenefits().stream()
                .map(BenefitRes::from)
                .toList();

        String description = benefitRes.stream()
                .map(b -> {
                    String gradeStr = b.grade() == Grade.GOOD ? "우수" : b.grade().name();
                    return gradeStr + " : " + b.description();
                })
                .collect(Collectors.joining(", "));

        return new BrandRes(
                brand.getId(),
                brand.getBrandName(),
                brand.getLogoImage(),
                description
        );
    }
}
