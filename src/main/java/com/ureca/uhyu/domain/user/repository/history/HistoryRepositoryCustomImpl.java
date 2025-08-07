package com.ureca.uhyu.domain.user.repository.history;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsMembershipUsageRes;
import com.ureca.uhyu.domain.admin.dto.MembershipUsageByBrand;
import com.ureca.uhyu.domain.brand.entity.QBrand;
import com.ureca.uhyu.domain.brand.entity.QCategory;
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

    @Override
    public List<StatisticsMembershipUsageRes> findStatisticsMembershipUsageByCategory() {
        QHistory history = QHistory.history;
        QStore store = QStore.store;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;

        // 1. DB에서 카테고리, 브랜드 단위로 집계된 결과 가져오기
        List<Tuple> tuples = queryFactory
                .select(
                        category.id,
                        category.categoryName,
                        brand.brandName,
                        history.count()
                )
                .from(history)
                .join(store).on(history.store.id.eq(store.id))
                .join(brand).on(store.brand.id.eq(brand.id))
                .join(category).on(brand.category.id.eq(category.id))
                .groupBy(category.id, category.categoryName, brand.brandName)
                .fetch();

        // 2. Java에서 categoryId → 브랜드 리스트, 총합 매핑
        Map<Long, List<MembershipUsageByBrand>> categoryGrouped = new LinkedHashMap<>();
        Map<Long, Integer> categorySumMap = new HashMap<>();
        Map<Long, String> categoryNameMap = new HashMap<>();

        for (Tuple t : tuples) {
            Long categoryId = t.get(0, Long.class);
            String categoryName = t.get(1, String.class);
            String brandName = t.get(2, String.class);
            Integer usageCount = t.get(3, Long.class).intValue();

            // 브랜드별 리스트 추가
            categoryGrouped
                    .computeIfAbsent(categoryId, k -> new ArrayList<>())
                    .add(MembershipUsageByBrand.of(brandName, usageCount));

            // 카테고리별 총합
            categorySumMap.merge(categoryId, usageCount, Integer::sum);
            categoryNameMap.putIfAbsent(categoryId, categoryName);
        }

        // 3. DTO 조립
        return categoryGrouped.entrySet().stream()
                .map(entry -> StatisticsMembershipUsageRes.of(
                        entry.getKey(),
                        categoryNameMap.get(entry.getKey()),
                        categorySumMap.get(entry.getKey()),
                        entry.getValue()
                ))
                .toList();
    }
}
