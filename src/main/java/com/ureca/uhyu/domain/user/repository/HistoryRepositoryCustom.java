package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.user.entity.User;

public interface HistoryRepositoryCustom {
    Integer findDiscountMoneyThisMonth(User user);
}
