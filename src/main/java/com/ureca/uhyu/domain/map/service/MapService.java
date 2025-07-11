package com.ureca.uhyu.domain.map.service;


import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
import com.ureca.uhyu.domain.user.entity.User;

import java.util.List;

public interface MapService {
    List<MapRes> getStoresInRange(Double lat, Double lon, Double radius);
    StoreDetailRes getStoreDetail(Long storeId, User user);
    List<com.ureca.uhyu.domain.map.dto.response.MapRes> getSearchedStoresInRange(Double lat, Double lon, Double radius, String brandName);
}