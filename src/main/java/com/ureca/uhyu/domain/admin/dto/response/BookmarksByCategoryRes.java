package com.ureca.uhyu.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카테고리별 즐겨찾기 통계 응답 DTO")
public record BookmarksByCategoryRes (
        Long categoryId,
        String categoryName,
        Integer sumBookmarksByCategory,
        List<BookmarksByBrandRes> bookmarksByBrandList
){
    public static BookmarksByCategoryRes of(Long categoryId, String categoryName, Integer sumBookmarksByCategory, List<BookmarksByBrandRes> bookmarksByBrandList){
        return new BookmarksByCategoryRes(
                categoryId,
                categoryName,
                sumBookmarksByCategory,
                bookmarksByBrandList
        );
    }
}
