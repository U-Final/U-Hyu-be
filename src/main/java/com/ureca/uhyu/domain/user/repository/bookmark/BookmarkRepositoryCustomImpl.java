package com.ureca.uhyu.domain.user.repository.bookmark;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByBrand;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsBookmarkMyMapRes;
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
}
