package com.ureca.uhyu.domain.admin.dto;

//통계 쿼리들 결과 저장할 중간 dto
public record StatisticsDto(
        String brandName,
        Long categoryId,
        String categoryName,
        int count
) {
}
