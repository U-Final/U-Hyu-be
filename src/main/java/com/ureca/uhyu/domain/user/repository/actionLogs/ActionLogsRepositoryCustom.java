package com.ureca.uhyu.domain.user.repository.actionLogs;

import com.ureca.uhyu.domain.admin.dto.response.StatisticsFilterByCategoryRes;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.ActionType;

import java.util.List;

public interface ActionLogsRepositoryCustom {
    List<Brand> findTop3ClickedBrands(User user);

    List<StatisticsFilterByCategoryRes> findStatisticsFilterByActionType(ActionType actionType);
}
