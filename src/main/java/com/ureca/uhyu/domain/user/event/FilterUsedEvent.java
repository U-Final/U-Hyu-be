package com.ureca.uhyu.domain.user.event;

import lombok.Getter;

@Getter
public class FilterUsedEvent {

    private final Long userId;
    private final Long categoryId;
    private final String categoryName;

    public FilterUsedEvent(Long userId, Long categoryId, String categoryName) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
