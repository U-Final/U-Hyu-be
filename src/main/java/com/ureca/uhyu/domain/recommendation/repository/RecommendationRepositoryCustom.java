package com.ureca.uhyu.domain.recommendation.repository;

import com.ureca.uhyu.domain.admin.dto.response.StatisticsRecommendationRes;
import com.ureca.uhyu.domain.recommendation.entity.Recommendation;

import java.util.List;

public interface RecommendationRepositoryCustom {
    List<Recommendation> findTop3ByUserOrderByCreatedAtDescRankAsc(Long userId);

    List<StatisticsRecommendationRes> findStatisticsRecommendationByCategory();
}
