package com.ureca.uhyu.domain.guest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비로그인 사용자 추천 응답 dto")
public record GuestRecommendationRes (
        Long brandId,
        String brandName,
        String logoImg
){
    public static GuestRecommendationRes from(Long brandId, String brandName, String logoImg){
        return new GuestRecommendationRes(brandId, brandName, logoImg);
    }
}
