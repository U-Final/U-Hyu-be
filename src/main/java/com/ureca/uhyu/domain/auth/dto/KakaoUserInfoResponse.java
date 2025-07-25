package com.ureca.uhyu.domain.auth.dto;

import com.ureca.uhyu.domain.user.enums.Gender;

public record KakaoUserInfoResponse(
        Long kakaoId,
        String nickname,
        String email,
        String name,
        String profileImage,
        Gender gender,
        String age_range,
        String birthyear
) {}