package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.store.entity.Store;

public record RecentStoreListRes(
        Long recentStoreId,
        String recentStoreName,
        String recentBrandImage
) {
    public static RecentStoreListRes from(Store store) {
        return new RecentStoreListRes(
                store.getId(),
                store.getName(),
                store.getBrand().getLogoImage()
        );
    }
}
