package com.ureca.uhyu.domain.store.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.brand.entity.QBenefit;
import com.ureca.uhyu.domain.brand.entity.QBrand;
import com.ureca.uhyu.domain.brand.entity.QCategory;
import com.ureca.uhyu.domain.store.entity.QStore;
import com.ureca.uhyu.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryCustomImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QStore store = QStore.store;
    private final QBrand brand = QBrand.brand;
    private final QCategory category = QCategory.category;
    private final QBenefit benefit = QBenefit.benefit;

    private Predicate withinRadius(double lat, double lon, double radius) {
        return Expressions.booleanTemplate(
                "cast(ST_DWithin(" +
                        "ST_Transform({0}, 3857), " +
                        "ST_Transform(ST_SetSRID(ST_MakePoint({1}, {2}), 4326), 3857), " +
                        "{3}) as boolean)",
                store.geom, lon, lat, radius
        );
    }

    private JPAQuery<Store> baseStoreQuery() {
        return queryFactory
                .selectFrom(store)
                .leftJoin(store.brand, brand).fetchJoin()
                .leftJoin(brand.category, category).fetchJoin()
                .leftJoin(brand.benefits, benefit).fetchJoin()
                .distinct();
    }

    @Override
    public List<Store> findStoresByFilters(Double lat, Double lon, Double radius, String categoryName, String brandName) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(withinRadius(lat, lon, radius));

        if (categoryName != null && !categoryName.isBlank()) {
            builder.and(category.categoryName.containsIgnoreCase(categoryName));
        }

        if (brandName != null && !brandName.isBlank()) {
            builder.and(brand.brandName.containsIgnoreCase(brandName));
        }

        return baseStoreQuery()
                .where(builder)
                .fetch();
    }

    @Override
    public List<Store> findStoresByBrandAndRadius(Double lat, Double lon, Double radius, List<Long> brandIds) {
        if (brandIds == null || brandIds.isEmpty()) return List.of();

        BooleanBuilder builder = new BooleanBuilder();

        // 위치 필터
        builder.and(withinRadius(lat, lon, radius));

        // 추천 브랜드 필터
        builder.and(brand.id.in(brandIds));

        return baseStoreQuery()
                .where(builder)
                .fetch();
    }
}
