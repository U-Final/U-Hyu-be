package com.ureca.uhyu.domain.recommendation.dto.response;

import com.ureca.uhyu.domain.recommendation.entity.Recommendation;

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
