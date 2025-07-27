package com.ureca.uhyu.domain.admin.dto.response;

public record StatisticsFilterByCategoryRes(
        Long categoryId,
        String categoryName,
        Integer sumStatisticsFilterByCategory
){
    public static StatisticsFilterByCategoryRes of(Long categoryId, String categoryName, Integer sumStatisticsFilterByCategory){
        return new StatisticsFilterByCategoryRes(
                categoryId,
                categoryName,
                sumStatisticsFilterByCategory
        );
    }
}
