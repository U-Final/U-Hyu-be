package com.ureca.uhyu.domain.mymap.controller;

import com.ureca.uhyu.domain.mymap.dto.request.MyMapReq;
import com.ureca.uhyu.domain.mymap.dto.response.MyMapRes;
import com.ureca.uhyu.domain.mymap.service.MyMapService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mymap")
@RequiredArgsConstructor
public class MyMapController {

    private final MyMapService myMapService;

    @Operation(summary = "My Map 조회", description = "어느 My Map에 저장할지 정하기 위해 목록 조회, 즐겨찾기는 프론트쪽에서 버튼만 만들기")
    @GetMapping("/list")
    public CommonResponse<List<MyMapRes>> getMyMapList(@CurrentUser User user){
        return CommonResponse.success(myMapService.findMyMapList(user));
    }

    @Operation(summary = "My Map 추가", description = "사용자가 새로운 My Map을 생성")
    @PostMapping
    public CommonResponse<Long> createMyMapList(@CurrentUser User user, @RequestBody MyMapReq myMapReq){
        return CommonResponse.success(myMapService.createMyMapList(user, myMapReq));
    }
}
