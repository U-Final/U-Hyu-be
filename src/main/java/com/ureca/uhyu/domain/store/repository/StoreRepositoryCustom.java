package com.ureca.uhyu.domain.store.repository;

import com.ureca.uhyu.domain.store.entity.Store;
import java.util.List;

public interface StoreRepositoryCustom {
    List<Store> findStoresInRadius(double lat, double lon, double radiusInKm);
    List<Store> findSearchedStoresInRadius(double lat, double lon, double radiusInKm, String brandName);
}