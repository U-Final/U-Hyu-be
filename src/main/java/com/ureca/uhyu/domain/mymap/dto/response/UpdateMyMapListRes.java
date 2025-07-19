package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;

public record UpdateMyMapListRes(
    Long myMapListId
) {
    public static UpdateMyMapListRes from(MyMapList myMapList) {
        return new UpdateMyMapListRes(myMapList.getId());
    }
}
