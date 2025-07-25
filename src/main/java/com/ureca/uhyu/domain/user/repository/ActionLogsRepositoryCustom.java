package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.admin.dto.response.CountFilterByCategoryRes;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.ActionType;

import java.util.List;

public interface ActionLogsRepositoryCustom {
    List<Brand> findTop3ClickedBrands(User user);

    List<CountFilterByCategoryRes> findCountFilterByActionType(ActionType actionType);
}
