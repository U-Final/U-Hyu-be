package com.ureca.uhyu.domain.admin.dto.response;

public record StatisticsFilterRes(
        Long categoryId,
        String categoryName,
        Integer sumStatisticsFilterByCategory
){
    public static StatisticsFilterRes of(Long categoryId, String categoryName, Integer sumStatisticsFilterByCategory){
        return new StatisticsFilterRes(
                categoryId,
                categoryName,
                sumStatisticsFilterByCategory
        );
    }
}
