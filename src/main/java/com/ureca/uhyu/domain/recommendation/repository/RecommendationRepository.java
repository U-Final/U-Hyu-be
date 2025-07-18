package com.ureca.uhyu.domain.recommendation.repository;

import com.ureca.uhyu.domain.recommendation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserId(Long userId);

    // 가장 최신 추천 시간 조회
    Optional<LocalTime> findTop1CreatedAtByUserIdOrderByCreatedAtDesc(Long userId);

    // 해당 날짜의 추천 중 rank 상위 3개
    List<Recommendation> findTop3ByUserIdAndCreatedAtOrderByRankAsc(Long userId, LocalTime createdAt);
}
