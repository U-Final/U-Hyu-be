package com.ureca.uhyu.domain.store.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.brand.entity.QBenefit;
import com.ureca.uhyu.domain.brand.entity.QBrand;
import com.ureca.uhyu.domain.brand.entity.QCategory;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.store.entity.QStore;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.enums.Grade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.types.Projections.constructor;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QStore store = QStore.store;
    private final QBrand brand = QBrand.brand;
    private final QCategory category = QCategory.category;
    private final QBenefit benefit = QBenefit.benefit;

    private Predicate withinRadius(double lat, double lon, double radiusInMeters) {
        // 미터(m) 단위의 radiusMeters를 degree(각도) 단위로 변환
        double radiusInDegrees = radiusInMeters / (111_320.0 * Math.cos(Math.toRadians(lat)));

        return Expressions.booleanTemplate(
                "CAST(ST_DWithin({0}, ST_SetSRID(ST_MakePoint({1}, {2}), 4326), {3}) AS boolean)",
                store.geom, lon, lat, radiusInDegrees
        );
    }

    @Override
    public List<MapRes> findStoresByFilters(Double lat, Double lon, Double radius, String categoryName, String brandName) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(withinRadius(lat, lon, radius));

        if (categoryName != null && !categoryName.isBlank()) {
            builder.and(category.categoryName.containsIgnoreCase(categoryName));
        }

        if (brandName != null && !brandName.isBlank()) {
            builder.and(brand.brandName.containsIgnoreCase(brandName));
        }

        // ✅ grade.GOOD에 해당하는 혜택 하나만 가져오기 위한 서브쿼리
        QBenefit subBenefit = new QBenefit("subBenefit");

        var benefitSubQuery = JPAExpressions
                .select(subBenefit.description)
                .from(subBenefit)
                .where(
                        subBenefit.brand.id.eq(brand.id),
                        subBenefit.grade.eq(Grade.GOOD)
                )
                .limit(1);

        // ✅ DTO로 바로 Projection
        return queryFactory
                .select(constructor(MapRes.class,
                        store.id,
                        store.name,
                        category.categoryName,
                        store.addrDetail,
                        benefitSubQuery, // 🟢 benefit을 조인하지 않고 서브쿼리로
                        brand.logoImage,
                        brand.brandName,
                        brand.id,
                        Expressions.numberTemplate(Double.class, "ST_Y({0})", store.geom),
                        Expressions.numberTemplate(Double.class, "ST_X({0})", store.geom)
                ))
                .from(store)
                .leftJoin(store.brand, brand)
                .leftJoin(brand.category, category)
                .where(builder)
                .fetch();
    }

    @Override
    public List<Store> findNearestStores(Double lat, Double lon, List<Long> brandIds) {
        if (brandIds == null || brandIds.isEmpty()) {
            return List.of();
        }

        // ST_Distance 사용으로 성능 개선 (구면 거리 계산 제거)
        NumberTemplate<Double> distanceExpr = Expressions.numberTemplate(Double.class,
                "ST_Distance({0}, ST_SetSRID(ST_MakePoint({1}, {2}), 4326))",
                store.geom, lon, lat
        );

        // 단일 쿼리로 모든 브랜드의 매장 조회
        List<Tuple> results = queryFactory
                .select(store, distanceExpr.as("distance"))
                .from(store)
                .leftJoin(store.brand, brand).fetchJoin()
                .leftJoin(brand.category, category).fetchJoin()
                .leftJoin(brand.benefits, benefit).fetchJoin()
                .where(store.brand.id.in(brandIds))
                .orderBy(store.brand.id.asc(), distanceExpr.asc())
                .fetch();

        // 브랜드별로 가장 가까운 매장만 선택 (LinkedHashMap으로 순서 보장)
        Map<Long, Store> nearestByBrand = new LinkedHashMap<>();

        for (Tuple tuple : results) {
            Store storeEntity = tuple.get(store);
            Long brandId = storeEntity.getBrand().getId();

            // 각 브랜드별로 첫 번째(가장 가까운) 매장만 저장
            if (!nearestByBrand.containsKey(brandId)) {
                nearestByBrand.put(brandId, storeEntity);
            }
        }

        return new ArrayList<>(nearestByBrand.values());
    }
}
