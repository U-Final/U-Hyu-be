package com.ureca.uhyu.domain.recommendation.service;

import com.ureca.uhyu.domain.recommendation.dto.response.RecommendationRes;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<RecommendationRes> getLatestTop3Recommendations(User user) {
        Long userId = user.getId();

        // 가장 최근 추천 받은 브랜드들 중 top3 추천 브랜드 가져오기
        return recommendationRepository.findTop3ByUserOrderByCreatedAtDescRankAsc(userId)
                .stream()
                .map(r -> {
                    if (r.getBrand() == null) {
                        throw new GlobalException((ResultCode.BRAND_ID_IS_NULL));
                    }
                    return new RecommendationRes(
                            r.getBrand().getId(),
                            r.getBrand().getBrandName(),
                            r.getRank()
                    );
                })
                .toList();
    }
}
