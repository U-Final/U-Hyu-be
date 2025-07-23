package com.ureca.uhyu.domain.recommendation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.recommendation.entity.QRecommendation;
import com.ureca.uhyu.domain.recommendation.entity.Recommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecommendationRepositoryCustomImpl implements RecommendationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Recommendation> findTop3ByUserOrderByCreatedAtDescRankAsc(Long userId) {
        QRecommendation recommendation = QRecommendation.recommendation;

        return queryFactory
                .selectFrom(recommendation)
                .where(recommendation.userId.eq(userId))
                .orderBy(
                        recommendation.createdAt.desc(),
                        recommendation.rank.asc()
                )
                .limit(3)
                .fetch();
    }
}
