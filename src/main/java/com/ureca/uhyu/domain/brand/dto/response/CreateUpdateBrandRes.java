package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "브랜드 생성, 수정 응답 dto")
public record CreateUpdateBrandRes(
        Long brandId
) {
    public static CreateUpdateBrandRes from(Brand brand) {
        return new CreateUpdateBrandRes(brand.getId());
    }
}
