package com.ureca.uhyu.domain.admin.dto.response;

public record StatisticsFilterByCategoryRes(
        Long categoryId,
        String categoryName,
        Integer sumCountFilterByCategory
){
    public static StatisticsFilterByCategoryRes of(Long categoryId, String categoryName, Integer sumCountFilterByCategory){
        return new StatisticsFilterByCategoryRes(
                categoryId,
                categoryName,
                sumCountFilterByCategory
        );
    }
}
