package com.ureca.uhyu.domain.mymap.dto.request;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "My Map 생성 요청 DTO")
public record CreateMyMapListReq(
        String title,
        MarkerColor markerColor,
        String uuid
) {
    public static CreateMyMapListReq from(MyMapList myMapList){
        return new CreateMyMapListReq(
                myMapList.getTitle(),
                myMapList.getMarkerColor(),
                myMapList.getUuid()
        );
    }
}
