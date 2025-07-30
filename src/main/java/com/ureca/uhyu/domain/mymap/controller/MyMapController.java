package com.ureca.uhyu.domain.mymap.controller;

import com.ureca.uhyu.domain.mymap.dto.request.CreateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.response.*;
import com.ureca.uhyu.domain.mymap.dto.request.UpdateMyMapListReq;
import com.ureca.uhyu.domain.mymap.service.MyMapService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "마이맵", description = "개인화된 지도 관리 관련 API")
@RestController
@RequestMapping("/mymap")
@RequiredArgsConstructor
public class MyMapController implements MyMapControllerDocs {

    private final MyMapService myMapService;

    @GetMapping("/list")
    public CommonResponse<List<MyMapListRes>> getMyMapList(@CurrentUser User user){
        return CommonResponse.success(myMapService.findMyMapList(user));
    }


    @PostMapping
    public CommonResponse<CreateMyMapListRes> createMyMapList(
            @CurrentUser User user,
            @Valid @RequestBody CreateMyMapListReq createMyMapListReq){
        return CommonResponse.success(myMapService.createMyMapList(user, createMyMapListReq));
    }


    @PatchMapping
    public CommonResponse<UpdateMyMapListRes> updateMyMapList(
            @CurrentUser User user,
            @Valid @RequestBody UpdateMyMapListReq updateMyMapListReq){
        return CommonResponse.success(myMapService.updateMyMapList(user, updateMyMapListReq));
    }


    @DeleteMapping("/{myMapListId}")
    public CommonResponse<ResultCode> deleteMyMapList(
            @CurrentUser User user,
            @PathVariable Long myMapListId) {
        myMapService.deleteMyMapList(user, myMapListId);
        return CommonResponse.success(ResultCode.MY_MAP_LIST_DELETE_SUCCESS);
    }


    @GetMapping("/{uuid}")
    public CommonResponse<MyMapRes> getMyMapByUUID(
            @CurrentUser User user,
            @PathVariable String uuid) {
        return CommonResponse.success(myMapService.findMyMapByUUID(user, uuid));
    }


    @GetMapping("/list/{store_id}")
    public CommonResponse<BookmarkedMyMapRes> getMyMapListWithIsBookmarked(
            @CurrentUser User user,
            @PathVariable(name = "store_id") Long storeId) {
        return CommonResponse.success(myMapService.findMyMapListWithIsBookmarked(user, storeId));
    }

    @PostMapping("/{myMapListId}/store/{store_id}")
    public CommonResponse<ToggleMyMapRes> toggleMyMap(
            @CurrentUser User user,
            @PathVariable Long myMapListId,
            @PathVariable(name = "store_id") Long storeId) {
        return CommonResponse.success(ResultCode.SUCCESS, myMapService.toggleMyMap(user, storeId, myMapListId));
    }
}
