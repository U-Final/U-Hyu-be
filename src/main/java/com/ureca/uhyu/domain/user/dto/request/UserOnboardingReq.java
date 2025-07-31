package com.ureca.uhyu.domain.user.dto.request;

import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "신규 사용자 온보딩 정보 요청 DTO")
public record UserOnboardingReq(

        @Schema(
                description = "사용자 나이",
                example = "26",
                allowableValues = "Integer",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "나이는 필수입니다.")
        Integer age,

        @Schema(
                description = "사용자 성별",
                example = "FEMALE",
                allowableValues = {"MALE", "FEMALE", "OTHER"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

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
                example = "[\"1\", \"2\", \"7\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "최근 이용 브랜드는 1개 이상 입력해야 합니다.")
        List<Long> recentBrands,

        @Schema(
                description = "관심 있는 브랜드 목록 (최소 1개 이상)",
                example = "[\"4\", \"9\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "관심 있는 브랜드는 1개 이상 입력해야 합니다.")
        List<Long> interestedBrands

) {}