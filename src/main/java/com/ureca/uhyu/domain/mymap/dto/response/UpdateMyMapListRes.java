package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "My Map 목록 수정 응답 DTO")
public record UpdateMyMapListRes(
    Long myMapListId
) {
    public static UpdateMyMapListRes from(MyMapList myMapList) {
        return new UpdateMyMapListRes(myMapList.getId());
    }
}
