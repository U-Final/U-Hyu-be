package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.mymap.enums.MarkerColor;

public record UpdateMyMapListRes(
    Long MyMapListId,
    String title,
    MarkerColor markerColor                 // ToDo : req를 enum으로 받아오던게 되던가요 기억이
) {
}
