package com.ureca.uhyu.domain.admin.dto.response;

public record BookmarksByBrandRes (
        String brandName,
        Integer sumBookmarksByBrand
){
    public static BookmarksByBrandRes of(String brandName, Integer sumBookmarksByBrand){
        return  new BookmarksByBrandRes(
                brandName,
                sumBookmarksByBrand
        );
    }
}
