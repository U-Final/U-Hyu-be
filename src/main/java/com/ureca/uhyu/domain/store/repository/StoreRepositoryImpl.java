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

    @Override
    public List<Store> findStoresInRadius(double lat, double lon, double radius) {
        QStore store = QStore.store;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;
        QBenefit benefit = QBenefit.benefit;

        return queryFactory
                .selectFrom(store)
                .leftJoin(store.brand, brand).fetchJoin()
                .leftJoin(brand.category, category).fetchJoin()
                .leftJoin(brand.benefits, benefit).fetchJoin()
                .where(
                        Expressions.booleanTemplate(
                                "cast(ST_DWithin(" +
                                        "ST_Transform({0}, 3857), " +
                                        "ST_Transform(ST_SetSRID(ST_MakePoint({1}, {2}), 4326), 3857), " +
                                        "{3}) as boolean)",
                                store.geom, lon, lat, radius
                        )
                )
                .distinct()
                .fetch();
    }

    @Override
    public List<Store> findSearchedStoresInRadius(double lat, double lon, double radius, String brandName) {
        QStore store = QStore.store;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;
        QBenefit benefit = QBenefit.benefit;

        return queryFactory
                .selectFrom(store)
                .leftJoin(store.brand, brand).fetchJoin()
                .leftJoin(brand.category, category).fetchJoin()
                .leftJoin(brand.benefits, benefit).fetchJoin()
                .where(
                        Expressions.booleanTemplate(
                                "cast(ST_DWithin(" +
                                        "ST_Transform({0}, 3857), " +
                                        "ST_Transform(ST_SetSRID(ST_MakePoint({1}, {2}), 4326), 3857), " +
                                        "{3}) as boolean)",
                                store.geom, lon, lat, radius
                        ),
                        brand.brandName.containsIgnoreCase(brandName)
                )
                .distinct()
                .fetch();
    }

    @Override
    public List<Store> findCategoryStoresInRadius(Double lat, Double lon, Double radius, String categoryName) {
        QStore store = QStore.store;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;
        QBenefit benefit = QBenefit.benefit;

        return queryFactory
                .selectFrom(store)
                .leftJoin(store.brand, brand).fetchJoin()
                .leftJoin(brand.category, category).fetchJoin()
                .leftJoin(brand.benefits, benefit).fetchJoin()
                .where(
                        Expressions.booleanTemplate(
                                "cast(ST_DWithin(" +
                                        "ST_Transform({0}, 3857), " +
                                        "ST_Transform(ST_SetSRID(ST_MakePoint({1}, {2}), 4326), 3857), " +
                                        "{3}) as boolean)",
                                store.geom, lon, lat, radius
                        ),
                        category.categoryName.containsIgnoreCase(categoryName)
                )
                .distinct()
                .fetch();
    }
}
