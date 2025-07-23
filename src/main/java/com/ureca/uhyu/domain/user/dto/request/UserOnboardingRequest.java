package com.ureca.uhyu.domain.user.dto.request;

import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "신규 사용자 온보딩 정보 요청 DTO")
public record UserOnboardingRequest(

        @Schema(
                description = "사용자 등급",
                example = "VIP",
                allowableValues = {"VVIP", "VIP", "GOOD"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "등급은 필수입니다.")
        Grade grade,

        @Schema(
                description = "최근 이용한 브랜드 목록 (최소 1개 이상)",
                example = "[\"스타벅스\", \"맥도날드\", \"버거킹\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "최근 이용 브랜드는 1개 이상 입력해야 합니다.")
        List<String> recentBrands,

        @Schema(
                description = "관심 있는 브랜드 목록 (최소 1개 이상)",
                example = "[\"투썸플레이스\", \"KFC\", \"도미노피자\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "관심 있는 브랜드는 1개 이상 입력해야 합니다.")
        List<String> interestedBrands

) {}