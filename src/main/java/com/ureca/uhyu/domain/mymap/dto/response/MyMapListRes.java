package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "My Map 목록 조회 응답 DTO")
public record MyMapListRes(
        Long myMapListId,
        String title,
        MarkerColor markerColor,
        String uuid
) {
    public static MyMapListRes from(MyMapList myMapList){
        return new MyMapListRes(
                myMapList.getId(),
                myMapList.getTitle(),
                myMapList.getMarkerColor(),
                myMapList.getUuid()
        );
    }
}
