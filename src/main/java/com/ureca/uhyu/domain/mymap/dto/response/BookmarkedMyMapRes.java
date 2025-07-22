package com.ureca.uhyu.domain.mymap.dto.response;

import com.ureca.uhyu.domain.store.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "My Map 매장 등록 유무 조회 응답 DTO")
public record BookmarkedMyMapRes (
        String storeName,
        List<BookmarkedMyMapListRes> bookmarkedMyMapLists,
        Boolean isBookmarked
){
    public static BookmarkedMyMapRes from(Store store, List<BookmarkedMyMapListRes> bookmarkedMyMapLists, boolean isBookmarked){
        return new BookmarkedMyMapRes(
                store.getName(),
                bookmarkedMyMapLists,
                isBookmarked
        );
    }
}
