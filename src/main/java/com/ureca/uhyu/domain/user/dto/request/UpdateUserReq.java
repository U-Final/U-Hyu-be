package com.ureca.uhyu.domain.user.dto.request;

import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사용자 정보 수정 요청 DTO")
public record UpdateUserReq(
        @Schema(
                description = "수정할 프로필 이미지 URL",
                example = "https://example.com/new-profile.jpg"
        )
        String updatedProfileImage,
        
        @Schema(
                description = "수정할 닉네임",
                example = "새로운닉네임",
                maxLength = 20
        )
        String updatedNickName,
        
        @Schema(
                description = "수정할 사용자 등급",
                example = "VIP",
                allowableValues = {"VVIP", "VIP", "GOOD"}
        )
        Grade updatedGrade,
        
        @Schema(
                description = "수정할 관심 브랜드 ID 목록",
                example = "[1, 2, 3]"
        )
        List<Long> updatedBrandIdList,
        
        @Schema(
                description = "수정할 마커 ID",
                example = "1"
        )
        Long markerId
) {
}
