package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.user.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Integer>
    , HistoryRepositoryCustom {

}
