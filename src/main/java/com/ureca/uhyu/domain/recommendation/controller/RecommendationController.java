package com.ureca.uhyu.domain.recommendation.controller;

import com.ureca.uhyu.domain.recommendation.dto.RecommendationResponse;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.recommendation.service.RecommendationService;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/user")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "추천 결과 조회", description = "최근 추천에서 rank 3위까지의 브랜드를 brandName과 함께 반환")
    @GetMapping("/recommendation")
    public CommonResponse<List<RecommendationResponse>> recommendation(@CurrentUser User user) {

        List<RecommendationResponse> result = recommendationService.getLatedstTop3Recommendations(user);

        return CommonResponse.success(result);
    }
}
