package com.ureca.uhyu.domain.user.event;

import lombok.Getter;

@Getter
public class MembershipUsedEvent {

    private final Long userId;
    private final Long storeId;
    private final Long brandId;
    private final String brandName;
    private final Long categoryId;
    private final String categoryName;

    public MembershipUsedEvent(Long userId, Long storeId, Long brandId, String brandName,
                               Long categoryId, String categoryName) {
        this.userId = userId;
        this.storeId = storeId;
        this.brandId = brandId;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
