package com.ureca.uhyu.domain.user.repository;


import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Set<UserBrandPair> findUserBrandSaves() {
        QBookmark bookmark = QBookmark.bookmark;
        QBookmarkList bookmarkList = QBookmarkList.bookmarkList;
        QMyMap myMap = QMyMap.myMap;
        QMyMapList myMapList = QMyMapList.myMapList;
        QStore store = QStore.store;

        List<Tuple> bookmarkTuples = queryFactory
                .select(bookmarkList.user.id, store.brand.id)
                .from(bookmark)
                .join(bookmark.bookmarkList, bookmarkList)
                .join(bookmark.store, store)
                .fetch();

        List<Tuple> myMapTuples = queryFactory
                .select(myMapList.user.id, store.brand.id)
                .from(myMap)
                .join(myMap.myMapList, myMapList)
                .join(myMap.store, store)
                .fetch();

        Set<UserBrandPair> savedPairs = new HashSet<>();
        bookmarkTuples.forEach(t -> savedPairs.add(new UserBrandPair(t.get(0, Long.class), t.get(1, Long.class))));
        myMapTuples.forEach(t -> savedPairs.add(new UserBrandPair(t.get(0, Long.class), t.get(1, Long.class))));

        return savedPairs;
    }

    @Override
    public Map<Long, Tuple> findBrandToCategoryMap() {
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;

        return queryFactory
                .select(
                        brand.id,
                        category.id,
                        category.categoryName,
                        brand.brandName
                )
                .from(brand)
                .join(brand.category, category)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        t -> t.get(0, Long.class), // brandId
                        t -> t                     // Tuple(brandId, categoryId, categoryName, brandName)
                ));
    }
}
