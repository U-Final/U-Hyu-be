package com.ureca.uhyu.domain.map.controller;

import com.ureca.uhyu.domain.map.dto.response.MapBookmarkRes;
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

    @Operation(summary = "지도 매장 겁색", description = "브랜드명 검색, 필터링, 기본 조회 API")
    @GetMapping("/stores")
    public CommonResponse<List<MapRes>> getFilteredStores(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radius,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand
    ) {
        return CommonResponse.success(ResultCode.SUCCESS, mapService.getFilteredStores(lat, lon, radius, category, brand));
    }

    @Operation(summary = "매장 상세정보 조회", description = "지도 마커를 클릭하면 등급별 혜택/제공 횟수/이용방법을 반환")
    @GetMapping("/{storeId}")
    public CommonResponse<StoreDetailRes> getStoreDetail(
            @PathVariable Long storeId,
            @CurrentUser User user
    ){
        return CommonResponse.success(ResultCode.SUCCESS, mapService.getStoreDetail(storeId,user));
    }

    @Operation(summary = "매장 즐겨찾기 토글", description = "매장 상세정보에서 즐겨찾기 토글 버튼 API")
    @PostMapping("/{storeId}")
    public CommonResponse<MapBookmarkRes> toggleBookmark(
            @PathVariable Long storeId,
            @CurrentUser User user
    ) {
        return CommonResponse.success(ResultCode.SUCCESS, mapService.toggleBookmark(user, storeId));
    }
}
