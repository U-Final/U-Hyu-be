package com.ureca.uhyu.domain.recommendation.repository;

import com.ureca.uhyu.domain.recommendation.entity.RecommendationBaseData;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationBaseDataRepository extends JpaRepository<RecommendationBaseData, Long> {
    void deleteByUserAndDataType(User user, DataType dataType);
}
