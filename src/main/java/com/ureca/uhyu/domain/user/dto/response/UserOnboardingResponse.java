package com.ureca.uhyu.domain.user.dto.response;

public record UserOnboardingResponse(
        Long userId,
        String accessToken,
        String refreshToken
) {}