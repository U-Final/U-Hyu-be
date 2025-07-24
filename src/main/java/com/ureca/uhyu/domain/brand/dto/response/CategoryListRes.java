package com.ureca.uhyu.domain.brand.dto.response;

import com.ureca.uhyu.domain.brand.entity.Category;

public record CategoryListRes (
        Long categoryId,
        String categoryName
){
    public static CategoryListRes from(Category category) {
        return new CategoryListRes(category.getId(), category.getCategoryName());
    }
}
