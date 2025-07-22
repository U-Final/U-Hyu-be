package com.ureca.uhyu.domain.mymap.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마커 색상")
public enum MarkerColor {
    @Schema(description = "빨간색 마커")
    RED,
    
    @Schema(description = "주황색 마커")
    ORANGE,
    
    @Schema(description = "노란색 마커")
    YELLOW,
    
    @Schema(description = "초록색 마커")
    GREEN,
    
    @Schema(description = "보라색 마커")
    PURPLE
}
