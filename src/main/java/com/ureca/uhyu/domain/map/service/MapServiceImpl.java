package com.ureca.uhyu.domain.map.service;

import com.ureca.uhyu.domain.map.dto.Request.MapReq;
import com.ureca.uhyu.domain.map.dto.Response.MapRes;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService{

    private final StoreRepositoryCustom storeRepository;

    @Override
    public List<MapRes> getStoresInRange(MapReq req) {
        double lat = req.lat();
        double lon = req.lon();
        double radius = req.radius();

        List<Store> stores = storeRepository.findStoresInRadius(lat, lon, radius);

        return stores.stream()
                .map(MapRes::from)
                .toList();
    }
}
