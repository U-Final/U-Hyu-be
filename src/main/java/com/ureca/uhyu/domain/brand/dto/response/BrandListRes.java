package com.ureca.uhyu.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "제휴처 목록 조회 응답")
public record BrandListRes(
        List<BrandRes> brandList,
        boolean hasNext,
        int totalPages,
        int currentPage
) {
    public static BrandListRes from(List<BrandRes> list, boolean hasNext, int totalPages, int currentPage) {
        return new BrandListRes(list, hasNext, totalPages, currentPage);
    }
}
