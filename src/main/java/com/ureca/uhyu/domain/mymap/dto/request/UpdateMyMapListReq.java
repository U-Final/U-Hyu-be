package com.ureca.uhyu.domain.mymap.dto.request;

import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "My Map 수정 요청 DTO")
public record UpdateMyMapListReq(
    Long myMapListId,

    @Size(max = 20, message = "제목은 20자를 초과할 수 없습니다")
    String title,
    MarkerColor markerColor
) {
}
