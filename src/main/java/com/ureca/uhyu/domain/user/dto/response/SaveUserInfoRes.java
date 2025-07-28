package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 정보 저장 완료 응답")
public record SaveUserInfoRes (
        Long userId
){
    public static SaveUserInfoRes from(User user){
        return new SaveUserInfoRes(user.getId());
    }
}