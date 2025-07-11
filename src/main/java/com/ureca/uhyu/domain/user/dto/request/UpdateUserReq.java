package com.ureca.uhyu.domain.user.dto.request;

import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "유저 정보 수정 요청")
public record UpdateUserReq(
        String updatedProfileImage,
        String updatedNickName,
        Grade updatedGrade,
        List<Long> updatedBrandIdList,
        Long markerId
) {
}
