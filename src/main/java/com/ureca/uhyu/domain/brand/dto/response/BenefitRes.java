package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.user.enums.Grade;

public record BenefitRes(
        Grade grade,
        String description
) {
    public static BenefitRes from(Benefit benefit) {
        return new BenefitRes(
                benefit.getGrade(),
                benefit.getDescription()
        );
    }
}
