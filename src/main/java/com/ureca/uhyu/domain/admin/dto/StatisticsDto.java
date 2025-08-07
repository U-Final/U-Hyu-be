package com.ureca.uhyu.domain.admin.dto;

public record StatisticsDto(
        String brandName,
        Long categoryId,
        String categoryName,
        int count
) {
}
