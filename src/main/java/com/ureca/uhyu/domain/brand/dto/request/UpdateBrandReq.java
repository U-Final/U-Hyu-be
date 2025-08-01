package com.ureca.uhyu.domain.brand.dto.request;

import com.ureca.uhyu.domain.brand.dto.BenefitDto;
import com.ureca.uhyu.domain.brand.enums.StoreType;

import java.util.List;

public record UpdateBrandReq(
        String brandName,
        String brandImg,
        List<BenefitDto> data, // grade, benefit 리스트
        Long categoryId,
        String usageLimit,
        String usageMethod,
        StoreType storeType
) {
}
