package com.ureca.uhyu.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카테고리별 즐겨찾기 통계 응답 DTO")
public record StatisticsBookmarkRes(
        Long categoryId,
        String categoryName,
        Integer sumStatisticsBookmarksByCategory,
        List<BookmarksByBrand> bookmarksByBrandList
){
    public static StatisticsBookmarkRes of(Long categoryId, String categoryName, Integer sumStatisticsBookmarksByCategory, List<BookmarksByBrand> bookmarksByBrandList){
        return new StatisticsBookmarkRes(
                categoryId,
                categoryName,
                sumStatisticsBookmarksByCategory,
                bookmarksByBrandList
        );
    }
}
