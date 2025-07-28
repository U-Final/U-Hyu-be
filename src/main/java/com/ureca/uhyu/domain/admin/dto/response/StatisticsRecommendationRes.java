package com.ureca.uhyu.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카테고리, 브랜드별 추천 통계 응답 DTO")
public record StatisticsRecommendationRes(
        Long categoryId,
        String categoryName,
        Integer sumStatisticsRecommendationByCategory,
        List<RecommendationsByBrand> recommendationsByBrandList
){
    public static StatisticsRecommendationRes of(Long categoryId, String categoryName, Integer sumStatisticsRecommendationByCategory, List<RecommendationsByBrand> recommendationsByBrandList){
        return new StatisticsRecommendationRes(
                categoryId,
                categoryName,
                sumStatisticsRecommendationByCategory,
                recommendationsByBrandList
        );
    }
}
