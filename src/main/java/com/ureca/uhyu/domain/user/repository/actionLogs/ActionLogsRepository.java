package com.ureca.uhyu.domain.user.repository.actionLogs;

import com.ureca.uhyu.domain.user.entity.ActionLogs;
import com.ureca.uhyu.domain.user.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionLogsRepository extends JpaRepository<ActionLogs, Long>
        , ActionLogsRepositoryCustom{
    long countByActionType(ActionType actionType);
}
