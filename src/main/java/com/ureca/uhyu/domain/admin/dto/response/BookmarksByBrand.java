package com.ureca.uhyu.domain.admin.dto.response;

public record BookmarksByBrand(
        String brandName,
        Integer sumBookmarksByBrand
){
    public static BookmarksByBrand of(String brandName, Integer sumBookmarksByBrand){
        return  new BookmarksByBrand(
                brandName,
                sumBookmarksByBrand
        );
    }
}
