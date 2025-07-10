package com.ureca.uhyu.domain.map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지도 제휴매장 조회 요청 DTO")
public record MapReq(
        @Schema(description = "위도")
        Double lat,

        @Schema(description = "경도")
        Double lon,

        @Schema(description = "검색 반경")
        Double radius
) {}
