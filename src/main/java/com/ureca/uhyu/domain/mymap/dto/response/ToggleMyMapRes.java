package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.store.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "My Map 추가/해제 응답 DTO")
public record ToggleMyMapRes (
        Long myMapListId,

        Long storeId,

        boolean isMyMapped
){
    public static ToggleMyMapRes from(MyMapList myMapList, Store store, boolean isMyMapped) {
        return new ToggleMyMapRes(
                myMapList.getId(),
                store.getId(),
                isMyMapped
        );
    }
}
