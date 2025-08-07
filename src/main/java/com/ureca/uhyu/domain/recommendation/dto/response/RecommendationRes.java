package com.ureca.uhyu.domain.recommendation.dto.response;

import com.ureca.uhyu.domain.recommendation.entity.Recommendation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 맞춤 추천 조회")
public record RecommendationRes(
        Long brandId,
        String brandName,
        Integer rank
){
    public static RecommendationRes from(Recommendation recommendation) {
        return new RecommendationRes(
                recommendation.getBrand().getId(),
                recommendation.getBrand().getBrandName(),
                recommendation.getRank()
        );
    }
}
