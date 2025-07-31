package com.ureca.uhyu.domain.guest.controller;

import com.ureca.uhyu.domain.guest.service.GuestService;
import com.ureca.uhyu.domain.mymap.dto.response.MyMapRes;
import com.ureca.uhyu.domain.guest.dto.response.GuestRecommendationRes;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "비로그인", description = "비로그인 사용자 요청 관련 API")
@RestController
@RequestMapping("/guest")
@RequiredArgsConstructor
public class GuestController implements GuestControllerDocs {

    private final GuestService guestService;

    @Override
    @GetMapping("/mymap/{uuid}")
    public CommonResponse<MyMapRes> getMyMapByUUIDWithGuest(@PathVariable String uuid) {
        return CommonResponse.success(guestService.findMyMapByUUIDWithGuest(uuid));
    }

    @Override
    @GetMapping("/recommendation/top3")
    public CommonResponse<List<GuestRecommendationRes>> guestTop3Recommendation(){
        return CommonResponse.success(guestService.getTop3PopularBrandsForGuest());
    }
}
