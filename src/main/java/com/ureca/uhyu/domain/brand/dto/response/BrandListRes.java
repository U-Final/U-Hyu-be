package com.ureca.uhyu.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "즐겨찾기 전체 조회 응답")
public record BrandListRes (
        List<BrandRes> brandList,
        boolean hasNext
) {
    public static BrandListRes from(List<BrandRes> brandList,  boolean hasNext) {
        return new BrandListRes(brandList, hasNext);
    }
}
