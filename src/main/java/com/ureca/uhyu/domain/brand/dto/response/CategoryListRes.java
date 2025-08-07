package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 목록 조회 응답 dto")
public record CategoryListRes (
        Long categoryId,
        String categoryName
){
    public static CategoryListRes from(Category category) {
        return new CategoryListRes(category.getId(), category.getCategoryName());
    }
}
