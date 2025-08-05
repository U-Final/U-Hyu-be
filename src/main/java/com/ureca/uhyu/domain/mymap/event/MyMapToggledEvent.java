package com.ureca.uhyu.domain.mymap.event;

import lombok.Getter;

@Getter
public class MyMapToggledEvent {

    public enum Action {
        ADD, REMOVE
    }

    private final Long userId;
    private final Long storeId;
    private final Long myMapListId;
    private final Long brandId;
    private final String brandName;
    private final Long categoryId;
    private final String categoryName;
    private final Action action;

    public MyMapToggledEvent(Long userId, Long storeId, Long myMapListId, Long brandId, String brandName,
                             Long categoryId, String categoryName, Action action) {
        this.userId = userId;
        this.storeId = storeId;
        this.myMapListId = myMapListId;
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.action = action;
    }
}
