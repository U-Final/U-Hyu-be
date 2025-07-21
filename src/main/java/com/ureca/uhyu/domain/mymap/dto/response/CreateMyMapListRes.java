package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;

public record CreateMyMapListRes(
    Long myMapListId
) {
    public static CreateMyMapListRes from(MyMapList myMapList) {
        return new CreateMyMapListRes(myMapList.getId());
    }
}
