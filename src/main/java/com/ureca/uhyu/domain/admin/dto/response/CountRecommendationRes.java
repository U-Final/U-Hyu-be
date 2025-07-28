package com.ureca.uhyu.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카테고리, 브랜드별 추천 통계 응답 DTO")
public record CountRecommendationRes (
        Long categoryId,
        String categoryName,
        Integer sumRecommendationByCategory,
        List<RecommendationsByBrand> recommendationsByBrandList
){
    public static CountRecommendationRes of(Long categoryId, String categoryName, Integer sumRecommendationByCategory, List<RecommendationsByBrand> recommendationsByBrandList){
        return new CountRecommendationRes(
                categoryId,
                categoryName,
                sumRecommendationByCategory,
                recommendationsByBrandList
        );
    }
}
