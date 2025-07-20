package com.ureca.uhyu.domain.mymap.dto.request;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "My Map 생성 요청 DTO")
public record CreateMyMapListReq(
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 20, message = "제목은 20자를 초과할 수 없습니다")
        String title,

        @NotNull(message = "마커 색상은 필수입니다")
        MarkerColor markerColor,

        @NotBlank(message = "UUID는 필수입니다")
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
