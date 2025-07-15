package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.entity.User;

import java.util.List;

public interface HistoryRepositoryCustom {
    Integer findDiscountMoneyThisMonth(User user);
    List<Store> findTop3RecentStore(User user);
}
