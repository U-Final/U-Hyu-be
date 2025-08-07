package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "My Map 목록 생성 응답 DTO")
public record CreateMyMapListRes(
    Long myMapListId
) {
    public static CreateMyMapListRes from(MyMapList myMapList) {
        return new CreateMyMapListRes(myMapList.getId());
    }
}
