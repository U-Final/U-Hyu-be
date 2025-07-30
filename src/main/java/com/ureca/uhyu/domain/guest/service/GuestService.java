package com.ureca.uhyu.domain.guest.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.guest.dto.response.GuestRecommendationRes;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.mymap.dto.response.MyMapRes;
import com.ureca.uhyu.domain.mymap.entity.MyMap;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.repository.MyMapListRepository;
import com.ureca.uhyu.domain.mymap.repository.MyMapRepository;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestService {

    private final MyMapListRepository myMapListRepository;
    private final MyMapRepository myMapRepository;
    private final RecommendationRepository recommendationRepository;

    public MyMapRes findMyMapByUUIDWithGuest(String uuid) {
        MyMapList myMapList = myMapListRepository.findByUuid(uuid).orElseThrow(() -> new GlobalException(ResultCode.MY_MAP_LIST_NOT_FOUND));
        List<MyMap> myMaps = myMapRepository.findByMyMapList(myMapList);

        List<MapRes> storeList = myMaps.stream()
                .map(myMap -> MapRes.from(myMap.getStore()))
                .toList();

        return MyMapRes.from(myMapList, storeList, false);
    }


    public List<GuestRecommendationRes> getTop3PopularBrandsForGuest() {
        List<Brand> brands = recommendationRepository.findTop3BrandByVisitCountFromHistory();

        if (brands == null || brands.isEmpty()) {
            throw new GlobalException((ResultCode.NOT_FOUND_RECOMMENDATION_FOR_USER));
        }
        return brands.stream()
                .map(brand -> GuestRecommendationRes.from(
                        brand.getId(),
                        brand.getBrandName(),
                        brand.getLogoImage()
                ))
                .toList();
    }

}
