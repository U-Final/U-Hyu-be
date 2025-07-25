package com.ureca.uhyu.domain.user.repository.history;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.admin.dto.response.CountMembershipUsageRes;
import com.ureca.uhyu.domain.admin.dto.response.MembershipUsageByBrand;
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
    public List<CountMembershipUsageRes> findCountMembershipUsageByCategory() {
        QHistory history = QHistory.history;
        QStore store = QStore.store;
        QBrand brand = QBrand.brand;
        QCategory category = QCategory.category;

        // 브랜드 단위로 방문 집계
        List<Tuple> brandTuples = queryFactory
                .select(
                        brand.id,
                        brand.brandName,
                        brand.category.id,
                        brand.category.categoryName,
                        history.count()
                )
                .from(history)
                .join(store).on(history.store.id.eq(store.id))
                .join(brand).on(store.brand.id.eq(brand.id))
                .join(category).on(brand.category.id.eq(category.id))
                .groupBy(brand.id, brand.brandName, brand.category.id, brand.category.categoryName)
                .fetch();

        // 카테고리 ID → 브랜드 리스트, 총합 계산
        Map<Long, List<MembershipUsageByBrand>> categoryGrouped = new LinkedHashMap<>();
        Map<Long, Integer> categorySumMap = new HashMap<>();
        Map<Long, String> categoryNameMap = new HashMap<>();

        for (Tuple t : brandTuples) {
            Long categoryId = t.get(2, Long.class);
            String categoryName = t.get(3, String.class);
            String brandName = t.get(1, String.class);
            Integer brandCount = t.get(4, Long.class).intValue();

            categoryGrouped
                    .computeIfAbsent(categoryId, k -> new ArrayList<>())
                    .add(MembershipUsageByBrand.of(brandName, brandCount));

            categorySumMap.merge(categoryId, brandCount, Integer::sum);
            categoryNameMap.putIfAbsent(categoryId, categoryName);
        }

        // DTO 조립
        return categoryGrouped.entrySet().stream()
                .map(entry -> CountMembershipUsageRes.of(
                        entry.getKey(),                                 //카테고리 id
                        categoryNameMap.get(entry.getKey()),            //카테고리 이름
                        categorySumMap.get(entry.getKey()),             //카테고리별 사용횟수
                        entry.getValue()                                //브랜드별 사용횟수 리스트
                ))
                .toList();
    }
}
