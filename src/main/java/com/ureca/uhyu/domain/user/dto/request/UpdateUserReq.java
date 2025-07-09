package com.ureca.uhyu.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "유저 정보 수정 요청")
public record UpdateUserReq(
        String profileImage,
        String nickName,
        Long brandId
    ) {
    public UpdateUserReq(String profileImage, String nickName, Long brandId) {
        this.profileImage = profileImage;
        this.nickName = nickName;
        this.brandId = brandId;
    }
}
