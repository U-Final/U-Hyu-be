package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 액션 로그 저장 완료 응답")
public record ActionLogsRes(
        Long userId
){
    public static ActionLogsRes from(User user){
        return new ActionLogsRes(user.getId());
    }
}