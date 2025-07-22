package com.ureca.uhyu.domain.recommendation.dto;

import com.ureca.uhyu.domain.recommendation.entity.Recommendation;

public record RecommendationResponse(
        Long brandId,
        String brandName,
        Integer rank
){
    public static RecommendationResponse from(Recommendation recommendation) {
        return new RecommendationResponse(
                recommendation.getBrand().getId(),
                recommendation.getBrand().getBrandName(),
                recommendation.getRank()
        );
    }
}
