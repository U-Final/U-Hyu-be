package com.ureca.uhyu.domain.map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "즐겨찾기 추가/해제 응답 DTO")
public record MapBookmarkRes(
        @Schema(description = "매장 ID")
        Long storeId,

        @Schema(description = "즐겨찾기 여부")
        boolean isBookmarked
) {}
