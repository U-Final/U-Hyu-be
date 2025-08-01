package com.ureca.uhyu.domain.recommendation.controller;

import com.ureca.uhyu.domain.recommendation.dto.request.ExcludeBrandRequest;
import com.ureca.uhyu.domain.recommendation.dto.response.RecommendationRes;
import com.ureca.uhyu.domain.recommendation.service.RecommendationBaseDataService;
import com.ureca.uhyu.domain.recommendation.service.RecommendationService;
import com.ureca.uhyu.domain.user.dto.response.SaveUserInfoRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "추천", description = "사용자 맞춤 추천 관련 API")
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController implements RecommendationControllerDocs {

    private final RecommendationService recommendationService;
    private final RecommendationBaseDataService recommendationBaseDataService;

    @Override
    @GetMapping
    public CommonResponse<List<RecommendationRes>> recommendation(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user
    ) {
        return CommonResponse.success(recommendationService.getLatestTop3Recommendations(user));
    }

    @Override
    @PostMapping("/exclude")
    public CommonResponse<SaveUserInfoRes> excludeBrand(
            @CurrentUser User user,
            @RequestBody ExcludeBrandRequest request
    ) {
        return CommonResponse.success(recommendationBaseDataService.excludeBrand(user, request));
    }

//    @GetMapping("/exclude")
//    pub
}
