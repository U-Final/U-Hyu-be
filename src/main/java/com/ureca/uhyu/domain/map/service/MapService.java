package com.ureca.uhyu.domain.map.service;


import com.ureca.uhyu.domain.map.dto.Request.MapReq;
import com.ureca.uhyu.domain.map.dto.Response.MapRes;

import java.util.List;

public interface MapService {
    List<MapRes> getStoresInRange(MapReq req);
}