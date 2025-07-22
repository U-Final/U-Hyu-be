package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.mymap.entity.MyMap;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import com.ureca.uhyu.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "My Map 조회 응답 DTO")
public record MyMapRes (
    MarkerColor markerColor,
    String title,
    Long myMapListId,
    String uuid,
    List<MapRes> storeList,
    Boolean isMine
) {
    public static MyMapRes from(MyMapList myMapList, List<MapRes> storeList, boolean isMine) {
        return new MyMapRes(
                myMapList.getMarkerColor(),
                myMapList.getTitle(),
                myMapList.getId(),
                myMapList.getUuid(),
                storeList,
                isMine
        );
    }
}
