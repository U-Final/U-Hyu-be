package com.ureca.uhyu.domain.map.controller;

import com.ureca.uhyu.domain.map.dto.Request.MapReq;
import com.ureca.uhyu.domain.map.dto.Response.MapRes;
import com.ureca.uhyu.domain.map.service.MapService;
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
    @PostMapping
    public CommonResponse<List<MapRes>> getStoresInRange(@RequestBody MapReq req) {
        return CommonResponse.success(ResultCode.SUCCESS, mapService.getStoresInRange(req));
    }
}
