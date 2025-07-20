package com.ureca.uhyu.domain.mymap.controller;

import com.ureca.uhyu.domain.mymap.dto.request.CreateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.response.CreateMyMapListRes;
import com.ureca.uhyu.domain.mymap.dto.response.MyMapListRes;
import com.ureca.uhyu.domain.mymap.dto.request.UpdateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.response.MyMapRes;
import com.ureca.uhyu.domain.mymap.dto.response.UpdateMyMapListRes;
import com.ureca.uhyu.domain.mymap.service.MyMapService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mymap")
@RequiredArgsConstructor
public class MyMapController {

    private final MyMapService myMapService;

    @Operation(summary = "My Map List 조회", description = "어느 My Map에 저장할지 정하기 위해 목록 조회, 즐겨찾기는 프론트쪽에서 버튼만 만들기")
    @GetMapping("/list")
    public CommonResponse<List<MyMapListRes>> getMyMapList(@CurrentUser User user){
        return CommonResponse.success(myMapService.findMyMapList(user));
    }

    @Operation(summary = "My Map List 추가", description = "사용자가 새로운 My Map을 생성")
    @PostMapping
    public CommonResponse<CreateMyMapListRes> createMyMapList(
            @CurrentUser User user,
            @Valid @RequestBody CreateMyMapListReq createMyMapListReq){
        return CommonResponse.success(myMapService.createMyMapList(user, createMyMapListReq));
    }

    @Operation(summary = "My Map List 수정", description = "지도 이름 or 마커 색상 수정")
    @PatchMapping
    public CommonResponse<UpdateMyMapListRes> updateMyMapList(
            @CurrentUser User user,
            @Valid @RequestBody UpdateMyMapListReq updateMyMapListReq){
        return CommonResponse.success(myMapService.updateMyMapList(user, updateMyMapListReq));
    }

    @Operation(summary = "My Map List 삭제", description = "지정한 My Map List 삭제")
    @DeleteMapping("/{myMapListId}")
    public CommonResponse<ResultCode> deleteMyMapList(@CurrentUser User user, @PathVariable Long myMapListId) {
        myMapService.deleteMyMapList(user, myMapListId);
        return CommonResponse.success(ResultCode.MY_MAP_LIST_DELETE_SUCCESS);
    }

    @Operation(summary = "uuid 기반 My Map 지도 조회", description = "my map uuid 기반으로 지도 조회")
    @GetMapping("/public/{uuid}")
    public CommonResponse<MyMapRes> getMyMapByUuid(@CurrentUser User user, @PathVariable String uuid) {
        return CommonResponse.success(myMapService.findMyMap(user, uuid));
    }
}
