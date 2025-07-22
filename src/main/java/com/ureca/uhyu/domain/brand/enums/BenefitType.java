package com.ureca.uhyu.domain.brand.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "혜택 타입")
public enum BenefitType {
    @Schema(description = "할인 혜택")
    DISCOUNT,
    
    @Schema(description = "증정품 혜택")
    GIFT
}
