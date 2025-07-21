package com.ureca.uhyu.domain.mymap.dto.response;


import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;

public record BookmarkedMyMapListRes(
        Long myMapListId,
        MarkerColor markerColor,
        String title,
        Boolean isMyMapped
) {
    public static  BookmarkedMyMapListRes from(MyMapList myMapList, boolean isMyMapped){
        return new BookmarkedMyMapListRes(
                myMapList.getId(),
                myMapList.getMarkerColor(),
                myMapList.getTitle(),
                isMyMapped
        );
    }
}
