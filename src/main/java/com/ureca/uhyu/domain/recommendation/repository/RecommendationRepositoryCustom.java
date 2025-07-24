package com.ureca.uhyu.domain.recommendation.repository;


import com.ureca.uhyu.domain.recommendation.entity.Recommendation;

import java.util.List;

public interface RecommendationRepositoryCustom {
    List<Recommendation> findTop3ByUserOrderByCreatedAtDescRankAsc(Long userId);
}
