package com.ureca.uhyu.domain.mymap.controller;

import com.ureca.uhyu.domain.mymap.dto.response.MyMapListRes;
import com.ureca.uhyu.domain.mymap.service.MyMapService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mymap")
@RequiredArgsConstructor
public class MyMapController {

    private final MyMapService myMapService;

    @Operation(summary = "즐겨찾기 + mymap 조회", description = "즐겨찾기 시 어느 카테고리에 저장할지 정하기 위해 목록 보여주기")
    @GetMapping("/list")
    public CommonResponse<MyMapListRes> getMyMapList(@CurrentUser User user){
        return CommonResponse.success(myMapService.findMyMapList(user));
    }
}
