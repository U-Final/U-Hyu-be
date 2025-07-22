package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "혜택 정보 응답 DTO")
public record BenefitRes(
        @Schema(description = "적용되는 사용자 등급", example = "VIP")
        Grade grade,
        
        @Schema(description = "혜택 설명", example = "학생 할인 10%")
        String description
) {
    public static BenefitRes from(Benefit benefit) {
        return new BenefitRes(
                benefit.getGrade(),
                benefit.getDescription()
        );
    }
}
