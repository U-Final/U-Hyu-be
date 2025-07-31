package com.ureca.uhyu.domain.auth.dto;

public record KakaoUserInfoResponse(
        Long kakaoId,
        String nickname,
        String email,
        String profileImage
) {}