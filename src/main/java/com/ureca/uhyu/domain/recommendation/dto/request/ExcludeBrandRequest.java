package com.ureca.uhyu.domain.recommendation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "추천 받은 브랜드에 대해 싫어요 누른 사용자 정보 및 브랜드 정보 저장")
public record ExcludeBrandRequest(
        @Schema(
                description = "싫어요 누른 브랜드 id",
                example = "2"
        )
        @NotNull(message = "한 개의 브랜드 id가 선택되어야 합니다.")
        Long storeId
) {
    public static ExcludeBrandRequest from(Long storeId) {
        return new ExcludeBrandRequest(storeId);
    }
}
