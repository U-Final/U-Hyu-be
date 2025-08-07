package com.ureca.uhyu.domain.user.repository.history;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.store.entity.QStore;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.entity.QHistory;
import com.ureca.uhyu.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class HistoryRepositoryCustomImpl implements HistoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Integer findDiscountMoneyThisMonth(User user) {
        QHistory history = QHistory.history;

        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        return queryFactory
                .select(history.benefitPrice.sum())
                .from(history)
                .where(
                        history.user.eq(user),
                        history.visitedAt.between(start, end)
                )
                .fetchOne();
    }

    public List<Store> findRecentStoreInMonth(User user) {
        QHistory history = QHistory.history;
        QStore store = QStore.store;

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        return queryFactory
                .select(store)
                .from(history)
                .join(store).on(history.store.eq(store))
                .where(
                        history.user.eq(user),
                        history.visitedAt.after(oneMonthAgo)
                )
                .orderBy(history.visitedAt.desc())
                .limit(3)
                .fetch();
    }
}
