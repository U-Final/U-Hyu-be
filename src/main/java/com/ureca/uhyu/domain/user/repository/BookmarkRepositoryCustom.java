package com.ureca.uhyu.domain.user.repository;


import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.UserBrandPair;

import java.util.Map;
import java.util.Set;

public interface BookmarkRepositoryCustom {
    Set<UserBrandPair> findUserBrandSaves();
    Map<Long, Tuple> findBrandToCategoryMap();
}
