package com.ureca.uhyu.domain.brand.dto.request;

import com.ureca.uhyu.domain.brand.dto.BenefitDto;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateBrandReq(

        @NotNull(message = "브랜드명은 필수 입력값 입니다.")
        String brandName,
        String brandImg,
        List<BenefitDto> data,
        @NotNull(message = "카테고리 지정은 필수입니다.")
        Long categoryId,
        String usageLimit,
        String usageMethod,
        StoreType storeType
) {
    public static CreateBrandReq from(
            final String brandName,
            final String brandImg,
            final List<BenefitDto> data,
            final Long categoryId,
            final String usageLimit,
            final String usageMethod,
            final StoreType storeType
    ) {
        return new CreateBrandReq(
                brandName,
                brandImg,
                data,
                categoryId,
                usageLimit,
                usageMethod,
                storeType
        );
    }
}
