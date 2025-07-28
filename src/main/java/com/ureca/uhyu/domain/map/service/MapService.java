package com.ureca.uhyu.domain.map.service;


import com.ureca.uhyu.domain.map.dto.response.MapBookmarkRes;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
import com.ureca.uhyu.domain.user.entity.User;

import java.util.List;

public interface MapService {
    List<MapRes> getFilteredStores(Double lat, Double lon, Double radius, String category, String brand);
    StoreDetailRes getStoreDetail(Long storeId, User user);
    MapBookmarkRes toggleBookmark(User user, Long storeId);
    List<MapRes> findRecommendedStores(Double lat, Double lon, Double radius, User user);
}