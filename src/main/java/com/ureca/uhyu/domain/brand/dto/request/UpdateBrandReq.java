package com.ureca.uhyu.domain.brand.dto.request;

import com.ureca.uhyu.domain.brand.enums.BenefitType;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.user.enums.Grade;

import java.util.List;

public record UpdateBrandReq(
        Long brandId,
        String brandName,
        String brandImg,
        List<BenefitDto> data, // grade, benefit 리스트
        Long category_id,
        String usage_limit,
        String usage_method,
        StoreType store_type
) {
    public record BenefitDto(
            Grade grade,
            String description,
            BenefitType benefitType
    ) {}
}
