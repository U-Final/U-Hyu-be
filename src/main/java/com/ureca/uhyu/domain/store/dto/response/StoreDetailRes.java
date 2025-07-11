package com.ureca.uhyu.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "매장 상세정보 응답 DTO")
public record StoreDetailRes(

        @Schema(description = "매장명")
        String storeName,

        /** TODO 추후 즐겨찾기 관련 로직 구현 이후 추가
         * @Schema(description = "즐겨찾기 여부")
         * boolean isFavorite,
         *
         * @Schema(description = "즐겨찾기 수")
         * int favoriteCount,
        */

        @Schema(description = "선택된 혜택 (등급별 1개)")
        BenefitDetail benefits,

        @Schema(description = "제공 횟수")
        String usageLimit,

        @Schema(description = "이용 방법")
        String usageMethod
) {
    @Schema(description = "등급별 혜택 DTO")
    public record BenefitDetail(
            @Schema(description = "등급 (VVIP, VIP, 우수)")
            String grade,

            @Schema(description = "혜택 내용")
            String benefitText
    ) {}
}