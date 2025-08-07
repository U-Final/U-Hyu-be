package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "즐겨찾기 1개 요소 응답 DTO")
public record BookmarkRes (
        Long bookmarkId,
        Long storeId,
        String logoImage,
        String storeName,
        String addressDetail,
        String benefit
) {
    public static BookmarkRes from(Bookmark bookmark) {
        User user = bookmark.getBookmarkList().getUser();
        Store store = bookmark.getStore();
        Brand brand = store.getBrand();

        Grade grade = (user != null) ? user.getGrade() : Grade.GOOD;
        String benefit = brand.getBenefitDescriptionByGradeOrDefault(grade);

        return new BookmarkRes(
                bookmark.getId(),
                store.getId(),
                brand.getLogoImage(),
                store.getName(),
                store.getAddrDetail(),
                benefit
        );
    }
}
