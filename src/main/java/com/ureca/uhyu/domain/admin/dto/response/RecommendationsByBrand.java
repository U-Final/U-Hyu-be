package com.ureca.uhyu.domain.admin.dto.response;

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
