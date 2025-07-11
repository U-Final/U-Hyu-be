package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.entity.BookmarksList;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "즐겨찾기 1개 요소 응답 DTO")
public record BookmarkRes (
        Long bookmarksListId,
        Long storeId,
        String logoImage,
        String storeName,
        String addressDetail,
        String benefit
) {
    public static BookmarkRes from(BookmarksList bookmarksList) {
        Store store = bookmarksList.getStore();
        Brand brand = store.getBrand();

        // TODO : 헤택(Benefit) 회원 등급별로 표시되는건지 확인 후 로직 수정
        String benefit = null;

        return new BookmarkRes(
                bookmarksList.getId(),
                store.getId(),
                brand.getLogoImage(),
                store.getName(),
                store.getAddrDetail(),
                benefit
        );
    }
}
