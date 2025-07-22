package com.ureca.uhyu.domain.recommendation.service;

import com.ureca.uhyu.domain.recommendation.dto.RecommendationResponse;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<RecommendationResponse> getLatestTop3Recommendations(User user) {
        Long userId = user.getId();

        // 가장 최근에 추천된 (최신화가 반영된?) 브랜드 가져오기
        LocalDateTime latestCreatedAt = recommendationRepository
                .findTop1CreatedAtByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new GlobalException((ResultCode.NOT_FOUND_RECOMMENDATION_FOR_USER)));

        // 해당 시간의 top3 추천 브랜드 가져오기
        return recommendationRepository.findTop3ByUserIdAndCreatedAtOrderByRankAsc(userId, LocalDateTime.from(latestCreatedAt))
                .stream()
                .map(r -> {
                    if (r.getBrand() == null) {
                        throw new GlobalException((ResultCode.BRAND_ID_IS_NULL));
                    }
                    return new RecommendationResponse(
                            r.getBrand().getId(),
                            r.getBrand().getBrandName(),
                            r.getRank()
                    );
                })
                .toList();
    }
}
