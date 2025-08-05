package com.ureca.uhyu.domain.admin.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface StatisticsRepositoryCustom {

    List<Tuple> findBookmarkMyMapStatistics();
    List<Tuple> findFilterStatistics();
    List<Tuple> findRecommendationStatistics();
    List<Tuple> findMembershipUsageStatistics();
}
