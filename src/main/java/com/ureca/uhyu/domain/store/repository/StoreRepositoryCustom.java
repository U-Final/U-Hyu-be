package com.ureca.uhyu.domain.store.repository;

import com.ureca.uhyu.domain.store.entity.Store;
import java.util.List;

public interface StoreRepositoryCustom {
    List<Store> findStoresByFilters(Double lon, Double lat, Double radius, String categoryName, String brandName);

    List<Store> findNearestStores(Double lat, Double lon, List<Long> brandIds);
}