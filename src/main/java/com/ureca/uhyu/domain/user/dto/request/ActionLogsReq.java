package com.ureca.uhyu.domain.user.dto.request;

import com.ureca.uhyu.domain.user.enums.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 액션 로그 저장 요청")
public record ActionLogsReq (
        ActionType actionType,
        Long storeId,
        Long categoryId
){}
