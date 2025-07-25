package com.ureca.uhyu.domain.admin.dto.response;

public record CountFilterByCategoryRes(
        Long categoryId,
        String categoryName,
        Integer sumBookmarksByCategory
){
    public static CountFilterByCategoryRes of(Long categoryId, String categoryName, Integer sumBookmarksByCategory){
        return new CountFilterByCategoryRes(
                categoryId,
                categoryName,
                sumBookmarksByCategory
        );
    }
}
