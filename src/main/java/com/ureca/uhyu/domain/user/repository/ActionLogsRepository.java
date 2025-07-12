package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.user.entity.ActionLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogsRepository extends JpaRepository<ActionLogs, Long> {
}
