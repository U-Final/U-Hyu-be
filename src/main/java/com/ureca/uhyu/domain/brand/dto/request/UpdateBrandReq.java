package com.ureca.uhyu.domain.brand.dto.request;

import com.ureca.uhyu.domain.brand.dto.BenefitDto;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

@Schema(description = "어드민 브랜드 수정 요청 DTO")
public record UpdateBrandReq(

        @Schema(
            description = "수정할 브랜드 이름",
            example = "카카오 프렌즈 리뉴얼"
        )
        Optional<String> brandName,

        @Schema(
            description = "수정할 브랜드 이미지 URL",
            example = "https://example.com/new-profile.jpg"
        )
        Optional<String> brandImg,

        @Schema(
            description = "수정할 브랜드 혜택 정보 리스트",
            example = "[{\"grade\": \"BEST\", \"description\": \"20% 할인 제공\", \"benefitType\": \"DISCOUNT\"}]"
        )
        Optional<List<BenefitDto>> data, // grade, benefit 리스트

        @Schema(
            description = "수정할 카테고리 ID",
            example = "2"
        )
        Optional<Long> categoryId,

        @Schema(
            description = "제휴 혜택 제공 횟수 정보",
            example = "월 1회"
        )
        Optional<String> usageLimit,

        @Schema(
            description = "혜택 이용 방법",
            example = "매장에서 제시"
        )
        Optional<String> usageMethod,

        @Schema(
            description = "혜택 제공 채널",
            example = "OFFLINE",
            allowableValues = {"ONLINE", "OFFLINE"}
        )
        Optional<StoreType> storeType
) {
}
