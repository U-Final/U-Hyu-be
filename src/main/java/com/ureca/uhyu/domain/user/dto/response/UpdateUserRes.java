package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 정보 수정 응답")
public record UpdateUserRes(
        Long userId
) {
    public static UpdateUserRes from(User user){
        return new UpdateUserRes(user.getId());
    }
}
