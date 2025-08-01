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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "지도", description = "지도 기반 매장 검색 및 즐겨찾기 관련 API")
@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController implements MapControllerDocs{

    private final MapService mapService;

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

    @GetMapping("/stores/bookmark")
    public CommonResponse<List<MapRes>> GetBookmarkedStores(
            @CurrentUser User user
    ){
        return CommonResponse.success(ResultCode.SUCCESS, mapService.getBookmarkedStores(user));
    }

    @GetMapping("/detail/stores/{storeId}")
    public CommonResponse<StoreDetailRes> getStoreDetail(
            @PathVariable Long storeId,
            @CurrentUser User user
    ){
        return CommonResponse.success(mapService.getStoreDetail(storeId,user));
    }

    @PostMapping("/{storeId}")
    public CommonResponse<MapBookmarkRes> toggleBookmark(
            @PathVariable Long storeId,
            @CurrentUser User user
    ) {
        return CommonResponse.success(mapService.toggleBookmark(user, storeId));
    }

    @Operation(summary = "추천 매장 조회", description = "사용자의 추천 브랜드에 해당하는 근처 매장 조회")
    @GetMapping("/recommendation/stores")
    public CommonResponse<List<MapRes>> getRecommendedStores(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @CurrentUser User user
    ) {
        return CommonResponse.success(mapService.findRecommendedStores(lat, lon, user));
    }
}
