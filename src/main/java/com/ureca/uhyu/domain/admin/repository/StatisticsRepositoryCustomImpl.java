package com.ureca.uhyu.domain.admin.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.admin.entity.QStatistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticsRepositoryCustomImpl implements StatisticsRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    QStatistics s = QStatistics.statistics;

    @Override
    public List<Tuple> findBookmarkMyMapStatistics() {
        return queryFactory
                .select(
                        s.brandName,
                        s.categoryId,
                        s.categoryName,
                        s.brandId,
                        s.userId.countDistinct() // user_id + brand_id 기준 중복 제거용
                )
                .from(s)
                .where(s.statisticsType.in(StatisticsType.BOOKMARK, StatisticsType.MYMAP))
                .groupBy(s.brandName, s.categoryId, s.categoryName, s.brandId)
                .fetch();
    }
}
