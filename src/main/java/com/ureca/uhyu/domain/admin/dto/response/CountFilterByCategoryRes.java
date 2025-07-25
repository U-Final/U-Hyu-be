package com.ureca.uhyu.domain.admin.dto.response;

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
