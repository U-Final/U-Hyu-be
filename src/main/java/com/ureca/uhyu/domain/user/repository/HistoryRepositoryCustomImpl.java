package com.ureca.uhyu.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.user.entity.QHistory;
import com.ureca.uhyu.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}
