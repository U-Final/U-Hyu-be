package com.ureca.uhyu.domain.admin.repository;

import com.ureca.uhyu.domain.admin.entity.Statistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface StatisticsRepository extends JpaRepository<Statistics, Long>, StatisticsRepositoryCustom {
    Long countByStatisticsTypeIn(Collection<StatisticsType> statisticsTypes);

    Long countByStatisticsType(StatisticsType statisticsType);

    void deleteByUserIdAndStoreIdAndMyMapListIdAndStatisticsType(Long userId, Long storeId, Long myMapListId, StatisticsType statisticsType);

    void deleteByUserIdAndStoreIdAndStatisticsType(Long userId, Long storeId, StatisticsType statisticsType);
}
