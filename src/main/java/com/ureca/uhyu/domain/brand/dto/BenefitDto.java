package com.ureca.uhyu.domain.brand.dto;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.enums.BenefitType;
import com.ureca.uhyu.domain.user.enums.Grade;

public record BenefitDto(
        Grade grade,
        String description,
        BenefitType benefitType
) {
    public static BenefitDto from(Benefit benefit) {
        return new BenefitDto(
                benefit.getGrade(),
                benefit.getDescription(),
                benefit.getBenefitType()
        );
    }
}
