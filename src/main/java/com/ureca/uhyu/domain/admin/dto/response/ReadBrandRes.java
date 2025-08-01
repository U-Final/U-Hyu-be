package com.ureca.uhyu.domain.admin.dto.response;

import com.ureca.uhyu.domain.brand.dto.BenefitDto;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.enums.StoreType;

import java.util.List;

public record ReadBrandRes (
        String brandName,
        String brandImg,
        List<BenefitDto> data,
        Long categoryId,
        String usageLimit,
        String usageMethod,
        StoreType storeType
){
    public static ReadBrandRes from(Brand brand
    ) {
        return new ReadBrandRes(
                brand.getBrandName(),
                brand.getLogoImage(),
                brand.getBenefits().stream().map(BenefitDto::from).toList(),
                brand.getCategory().getId(),
                brand.getUsageLimit(),
                brand.getUsageMethod(),
                brand.getStoreType()
        );
    }
}
