package com.ureca.uhyu.domain.user.dto.request;

import com.ureca.uhyu.domain.user.enums.Grade;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserOnboardingRequest(

        @NotNull(message = "등급은 필수입니다.")
        Grade grade,

        @NotEmpty(message = "최근 이용 브랜드는 1개 이상 입력해야 합니다.")
        List<String> recentBrands,

        @NotEmpty(message = "관심 있는 브랜드는 1개 이상 입력해야 합니다.")
        List<String> interestedBrands

) {}