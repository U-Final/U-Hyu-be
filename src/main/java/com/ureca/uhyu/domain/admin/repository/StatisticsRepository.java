package com.ureca.uhyu.domain.admin.repository;

import com.ureca.uhyu.domain.admin.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsRepository extends JpaRepository<Statistics, Long>, StatisticsRepositoryCustom {
}
