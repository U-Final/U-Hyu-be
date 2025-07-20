package com.ureca.uhyu.domain.mymap.dto.request;

import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMyMapListReq(
    Long myMapListId,

    @Size(max = 20, message = "제목은 20자를 초과할 수 없습니다")
    String title,
    MarkerColor markerColor
) {
}
