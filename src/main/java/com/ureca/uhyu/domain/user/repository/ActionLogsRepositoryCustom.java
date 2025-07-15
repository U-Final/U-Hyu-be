package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.user.entity.User;

import java.util.List;

public interface ActionLogsRepositoryCustom {
    List<Brand> findTop3ClickedBrands(User user);
}
