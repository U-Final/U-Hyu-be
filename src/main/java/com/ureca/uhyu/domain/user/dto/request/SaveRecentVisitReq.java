package com.ureca.uhyu.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SaveRecentVisitReq(

        @Schema(description = "최근 방문 매장 저장 요청 dto")
        @NotNull(message = "매장 Id는 필수 값입니다.")
        Long storeId
) {
}
