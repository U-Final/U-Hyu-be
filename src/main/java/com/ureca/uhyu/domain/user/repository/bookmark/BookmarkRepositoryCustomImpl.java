package com.ureca.uhyu.domain.user.repository.bookmark;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByBrand;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsBookmarkMyMapRes;
import com.ureca.uhyu.domain.admin.dto.response.UserBrandPair;
import com.ureca.uhyu.domain.brand.entity.QBrand;
import com.ureca.uhyu.domain.brand.entity.QCategory;
import com.ureca.uhyu.domain.mymap.entity.QMyMap;
import com.ureca.uhyu.domain.mymap.entity.QMyMapList;
import com.ureca.uhyu.domain.store.entity.QStore;
import com.ureca.uhyu.domain.user.entity.QBookmark;
import com.ureca.uhyu.domain.user.entity.QBookmarkList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StatisticsBookmarkMyMapRes> findStatisticsBookmarkByCategoryAndBrand() {
        QBookmark bookmark = QBookmark.bookmark;
        QBookmarkList bookmarkList = QBookmarkList.bookmarkList;
        QMyMap myMap = QMyMap.myMap;
        QMyMapList myMapList = QMyMapList.myMapList;
        QStore store = QStore.store;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;

        // bookmark 쿼리
        List<UserBrandPair> bookmarkPairs = queryFactory
                .select(Projections.constructor(UserBrandPair.class,
                        bookmarkList.user.id,
                        store.brand.id))
                .from(bookmark)
                .join(bookmark.bookmarkList, bookmarkList)
                .join(bookmark.store, store)
                .fetch();

        // myMap 쿼리
        List<UserBrandPair> myMapPairs = queryFactory
                .select(Projections.constructor(UserBrandPair.class,
                        myMapList.user.id,
                        store.brand.id))
                .from(myMap)
                .join(myMap.myMapList, myMapList)
                .join(myMap.store, store)
                .fetch();

        // 중복 제거된 user-brand 쌍 수집
        Set<UserBrandPair> allPairs = new HashSet<>();
        allPairs.addAll(bookmarkPairs);
        allPairs.addAll(myMapPairs);

        // 브랜드별 저장 수 카운트
        Map<Long, Integer> brandSaveCounts = allPairs.stream()
                .collect(Collectors.groupingBy(
                        UserBrandPair::brandId,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // 브랜드 → 카테고리 정보 조회
        Map<Long, Tuple> brandInfoMap = queryFactory
                .select(
                        brand.id,
                        brand.brandName,
                        category.id,
                        category.categoryName
                )
                .from(brand)
                .join(brand.category, category)
                .where(brand.id.in(brandSaveCounts.keySet()))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.get(0, Long.class),
                        t -> t
                ));

        // 카테고리 기준으로 그룹핑 및 DTO 조립
        Map<Long, StatisticsBookmarkMyMapRes> resultMap = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : brandSaveCounts.entrySet()) {
            Long brandId = entry.getKey();
            Integer count = entry.getValue();

            Tuple brandInfo = brandInfoMap.get(brandId);
            if (brandInfo == null) continue;

            String brandName = brandInfo.get(1, String.class);
            Long categoryId = brandInfo.get(2, Long.class);
            String categoryName = brandInfo.get(3, String.class);

            BookmarksByBrand bookmarksByBrand = BookmarksByBrand.of(brandName, count);

            resultMap.compute(categoryId, (key, existing) -> {
                if (existing == null) {
                    return StatisticsBookmarkMyMapRes.of(categoryId, categoryName, count, new ArrayList<>(List.of(bookmarksByBrand)));
                } else {
                    existing.bookmarksByBrandList().add(bookmarksByBrand);
                    int newSum = existing.sumStatisticsBookmarksByCategory() + count;
                    return StatisticsBookmarkMyMapRes.of(categoryId, categoryName, newSum, existing.bookmarksByBrandList());
                }
            });
        }

        return new ArrayList<>(resultMap.values());
    }
}
