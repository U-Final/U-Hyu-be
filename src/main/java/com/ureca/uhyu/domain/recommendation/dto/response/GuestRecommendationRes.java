package com.ureca.uhyu.domain.recommendation.dto.response;

public record GuestRecommendationRes (
        Long brandId,
        String brandName,
        String logoImg
){
    public static GuestRecommendationRes from(Long brandId, String brandName, String logoImg){
        return new GuestRecommendationRes(brandId, brandName, logoImg);
    }
}
