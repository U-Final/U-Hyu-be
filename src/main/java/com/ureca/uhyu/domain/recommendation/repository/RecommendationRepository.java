package com.ureca.uhyu.domain.recommendation.repository;

import com.ureca.uhyu.domain.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long>
        , RecommendationRepositoryCustom {

    List<Recommendation> findByUserId(Long userId);
}
