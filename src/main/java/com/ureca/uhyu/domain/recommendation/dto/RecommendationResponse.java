package com.ureca.uhyu.domain.recommendation.dto;

import com.ureca.uhyu.domain.recommendation.entity.Recommendation;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;

public record RecommendationResponse(
        String brandName,
        Integer rank
){
    public static RecommendationResponse from(Recommendation recommendation) {
        return new RecommendationResponse(
                recommendation.getBrandId().getBrandName(),
                recommendation.getRank()
        );
    }
}
