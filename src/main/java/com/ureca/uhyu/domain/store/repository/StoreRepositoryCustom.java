package com.ureca.uhyu.domain.store.repository;

import com.ureca.uhyu.domain.store.entity.Store;
import java.util.List;

public interface StoreRepositoryCustom {
    List<Store> findStoresByFilters(Double lat, Double lon, Double radius, String categoryName, String brandName);
}