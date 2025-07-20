package com.ureca.uhyu.domain.mymap.dto.request;

import com.ureca.uhyu.domain.mymap.enums.MarkerColor;

public record UpdateMyMapListReq(
    Long myMapListId,
    String title,
    MarkerColor markerColor
) {
}
