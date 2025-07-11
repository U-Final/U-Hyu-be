package com.ureca.uhyu.domain.recommendation.repository;

import com.ureca.uhyu.domain.recommendation.entity.RecommendationBaseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationBaseDataRepository extends JpaRepository<RecommendationBaseData, Long> {
}