package com.ureca.uhyu.domain.mymap.dto.response;


import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "My Map 목록에 들어있는지 여부를 포함하는 응답 dto")
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
