package com.ureca.uhyu.domain.brand.dto.request;

import com.ureca.uhyu.domain.brand.dto.BenefitDto;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateBrandReq(
        String brandName,
        String brandImg,
        List<BenefitDto> data, // grade, benefit 리스트
        @NotNull(message = "카테고리 지정은 필수입니다.")
        Long categoryId,
        String usageLimit,
        String usageMethod,
        StoreType storeType
) {
}
