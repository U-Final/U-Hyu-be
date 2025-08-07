package com.ureca.uhyu.domain.user.dto.request;

import com.ureca.uhyu.domain.user.enums.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유저 액션 로그 저장 요청")
public record ActionLogsReq (
        @NotNull(message = "액션 타입은 필수입니다.")
        ActionType actionType,
        Long storeId,
        Long categoryId
){}
