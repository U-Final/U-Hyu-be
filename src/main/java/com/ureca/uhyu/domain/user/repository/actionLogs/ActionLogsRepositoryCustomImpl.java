package com.ureca.uhyu.domain.user.repository.actionLogs;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.store.entity.QStore;
import com.ureca.uhyu.domain.user.entity.QActionLogs;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.ActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ActionLogsRepositoryCustomImpl implements ActionLogsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<Brand> findTop3ClickedBrands(User user) {
        QActionLogs actionLogs = QActionLogs.actionLogs;
        QStore store = QStore.store;

        return queryFactory
                .select(store.brand)
                .from(actionLogs)
                .join(store).on(actionLogs.storeId.eq(store.id))
                .where(
                        actionLogs.actionType.eq(ActionType.MARKER_CLICK),
                        actionLogs.user.eq(user)
                )
                .groupBy(store.brand)
                .orderBy(actionLogs.count().desc())
                .limit(3)
                .fetch();
    }
}
