package com.ureca.uhyu.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리별 필터 통계 응답 DTO")
public record CountFilterByCategoryRes(
        Long categoryId,
        String categoryName,
        Integer sumCountFilterByCategory
){
    public static CountFilterByCategoryRes of(Long categoryId, String categoryName, Integer sumCountFilterByCategory){
        return new CountFilterByCategoryRes(
                categoryId,
                categoryName,
                sumCountFilterByCategory
        );
    }
}
