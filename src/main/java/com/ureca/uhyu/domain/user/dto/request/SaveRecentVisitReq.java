package com.ureca.uhyu.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "최근 방문 매장 저장 요청 dto")
public record SaveRecentVisitReq(
        @NotNull(message = "매장 Id는 필수 값입니다.")
        Long storeId
) {
}
