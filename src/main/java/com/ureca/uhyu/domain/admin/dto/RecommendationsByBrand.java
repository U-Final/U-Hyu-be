package com.ureca.uhyu.domain.admin.dto;

public record RecommendationsByBrand(
        String brandName,
        Integer sumRecommendationsByBrand
) {
    public static RecommendationsByBrand of(String brandName, Integer sumRecommendationsByBrand){
        return  new RecommendationsByBrand(
                brandName,
                sumRecommendationsByBrand
        );
    }
}
