package com.ureca.uhyu.domain.map.event;

import lombok.Getter;

@Getter
public class BookmarkToggledEvent {

    public enum Action {
        ADD, REMOVE
    }

    private final Long userId;
    private final Long storeId;
    private final Long brandId;
    private final String brandName;
    private final Long categoryId;
    private final String categoryName;
    private final Action action;

    public BookmarkToggledEvent(Long userId, Long storeId, Long brandId, String brandName,
                                Long categoryId, String categoryName, Action action) {
        this.userId = userId;
        this.storeId = storeId;
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.action = action;
    }
}
