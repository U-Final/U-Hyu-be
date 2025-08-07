package com.ureca.uhyu.domain.recommendation.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsRecommendationRes;
import com.ureca.uhyu.domain.admin.dto.RecommendationsByBrand;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.QBrand;
import com.ureca.uhyu.domain.brand.entity.QCategory;
import com.ureca.uhyu.domain.recommendation.entity.QRecommendation;
import com.ureca.uhyu.domain.recommendation.entity.Recommendation;
import com.ureca.uhyu.domain.user.entity.QHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<StatisticsRecommendationRes> findStatisticsRecommendationByCategory() {
        QRecommendation recommendation = QRecommendation.recommendation;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;

        // 1. 브랜드별 추천 수 + 카테고리 정보
        List<Tuple> brandTuples = queryFactory
                .select(
                        category.id,
                        category.categoryName,
                        brand.brandName,
                        recommendation.id.count()
                )
                .from(recommendation)
                .join(brand).on(recommendation.brand.id.eq(brand.id))
                .join(category).on(brand.category.id.eq(category.id))
                .groupBy(category.id, category.categoryName, brand.brandName)
                .fetch();

        // 2. 카테고리별 추천 총합 + 카테고리명 포함
        Map<Long, Tuple> categoryTotalMap = queryFactory
                .select(
                        category.id,
                        category.categoryName,
                        recommendation.id.count().intValue()
                )
                .from(recommendation)
                .join(brand).on(recommendation.brand.id.eq(brand.id))
                .join(category).on(brand.category.id.eq(category.id))
                .groupBy(category.id, category.categoryName)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.get(0, Long.class),
                        t -> t                          // Tuple(categoryId, categoryName, totalCount)
                ));

        // 3. RecommendationsByBrand 구성
        Map<Long, List<RecommendationsByBrand>> brandGroupedByCategory = new LinkedHashMap<>();

        for (Tuple t : brandTuples) {
            Long categoryId = t.get(0, Long.class);
            String brandName = t.get(2, String.class);
            Integer brandCount = t.get(3, Long.class).intValue();

            brandGroupedByCategory
                    .computeIfAbsent(categoryId, k -> new ArrayList<>())
                    .add(RecommendationsByBrand.of(brandName, brandCount));
        }

        // 4. CountRecommendationRes 구성
        List<StatisticsRecommendationRes> result = new ArrayList<>();

        for (Map.Entry<Long, List<RecommendationsByBrand>> entry : brandGroupedByCategory.entrySet()) {
            Long categoryId = entry.getKey();
            List<RecommendationsByBrand> brandList = entry.getValue();

            Tuple categoryTuple = categoryTotalMap.get(categoryId);
            String categoryName = categoryTuple.get(1, String.class);
            Integer categoryTotal = categoryTuple.get(2, Integer.class);

            result.add(StatisticsRecommendationRes.of(categoryId, categoryName, categoryTotal, brandList));
        }

        return result;
    }

    @Override
    public List<Brand> findTop3BrandByVisitCountFromHistory() {
        QHistory history = QHistory.history;
        QBrand brand = QBrand.brand;

        return queryFactory
                .select(history.brand)
                .from(history)
                .where(history.brand.isNotNull())
                .groupBy(history.brand)
                .orderBy(history.count().desc())
                .limit(3)
                .fetch();
    }
}
