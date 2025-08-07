package com.ureca.uhyu.domain.admin.dto.response;

public record StatisticsDto(
        String brandName,
        Long categoryId,
        String categoryName,
        int count
) {
}
