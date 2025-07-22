package com.ureca.uhyu.domain.user.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 등급")
public enum Grade {
    @Schema(description = "VVIP 등급")
    VVIP,
    
    @Schema(description = "VIP 등급")
    VIP,
    
    @Schema(description = "GOOD 등급")
    GOOD
}
