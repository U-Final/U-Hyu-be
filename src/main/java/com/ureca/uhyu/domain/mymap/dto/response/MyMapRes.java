package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "즐겨찾기 + MyMap 목록 조회 응답 DTO")
public record MyMapRes(
        String title,
        MarkerColor markerColor
) {
    public static MyMapRes from(MyMapList myMapList){
        return new MyMapRes(
                myMapList.getTitle(),
                myMapList.getMarkerColor()
        );
    }
}
