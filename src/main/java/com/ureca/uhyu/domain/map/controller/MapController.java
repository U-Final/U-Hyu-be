package com.ureca.uhyu.domain.map.controller;

import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.map.service.MapService;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @Operation(summary = "범위 내 제휴 매장 조회", description = "중심 좌표와 반경을 기준으로 제휴 매장 조회")
    @GetMapping
    public CommonResponse<List<MapRes>> getStoresInRange(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radius
    ) {
        return CommonResponse.success(ResultCode.SUCCESS, mapService.getStoresInRange(lat, lon, radius));
    }

    @Operation(summary = "매장 상세정보 조회", description = "지도 마커를 클릭하면 등급별 혜택/제공 횟수/이용방법을 반환")
    @GetMapping("/{storeId}")
    public CommonResponse<StoreDetailRes> getStoreDetail(
            @PathVariable Long storeId,
            @CurrentUser User user
    ){
        return CommonResponse.success(ResultCode.SUCCESS, mapService.getStoreDetail(storeId,user));
    }

    @Operation(summary = "브랜드 검색", description = "브랜드 이름으로 검색 시 반경 내 해당 브랜드 목록 반환")
    @GetMapping("/brand")
    public CommonResponse<List<MapRes>> getSearchedStoresInRange(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radius,
            @RequestParam String brandName
    ) {
        System.out.println("브랜드명 검색 파라미터: " + brandName);
        return CommonResponse.success(ResultCode.SUCCESS, mapService.getSearchedStoresInRange(lat, lon, radius, brandName));
    }
}
