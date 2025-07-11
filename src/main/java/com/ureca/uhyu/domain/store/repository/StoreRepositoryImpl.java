package com.ureca.uhyu.domain.store.repository;

import com.querydsl.core.types.dsl.Expressions;
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
public class StoreRepositoryImpl implements StoreRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    private final QStore store = QStore.store;
    private final QBrand brand = QBrand.brand;
    private final QCategory category = QCategory.category;
    private final QBenefit benefit = QBenefit.benefit;

    private com.querydsl.core.types.Predicate withinRadius(double lat, double lon, double radius) {
        return Expressions.booleanTemplate(
                "cast(ST_DWithin(" +
                        "ST_Transform({0}, 3857), " +
                        "ST_Transform(ST_SetSRID(ST_MakePoint({1}, {2}), 4326), 3857), " +
                        "{3}) as boolean)",
                store.geom, lon, lat, radius
        );
    }

    private JPAQueryFactory baseQuery() {
        return queryFactory;
    }

    private com.querydsl.jpa.impl.JPAQuery<Store> baseStoreQuery() {
        return queryFactory
                .selectFrom(store)
                .leftJoin(store.brand, brand).fetchJoin()
                .leftJoin(brand.category, category).fetchJoin()
                .leftJoin(brand.benefits, benefit).fetchJoin()
                .distinct();
    }

    @Override
    public List<Store> findStoresInRadius(double lat, double lon, double radius) {
        return baseStoreQuery()
                .where(withinRadius(lat, lon, radius))
                .fetch();
    }

    @Override
    public List<Store> findSearchedStoresInRadius(double lat, double lon, double radius, String brandName) {
        return baseStoreQuery()
                .where(
                        withinRadius(lat, lon, radius),
                        brand.brandName.containsIgnoreCase(brandName)
                )
                .fetch();
    }

    @Override
    public List<Store> findCategoryStoresInRadius(Double lat, Double lon, Double radius, String categoryName) {
        return baseStoreQuery()
                .where(
                        withinRadius(lat, lon, radius),
                        category.categoryName.containsIgnoreCase(categoryName)
                )
                .fetch();
    }
}
