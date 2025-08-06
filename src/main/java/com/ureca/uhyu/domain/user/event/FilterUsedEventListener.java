package com.ureca.uhyu.domain.user.event;

import com.ureca.uhyu.domain.admin.entity.Statistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import com.ureca.uhyu.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilterUsedEventListener {

    private final AdminService adminService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFilterUsedEvent(FilterUsedEvent event) {
        Statistics statistics = Statistics.builder()
                .userId(event.getUserId())
                .categoryId(event.getCategoryId())
                .categoryName(event.getCategoryName())
                .statisticsType(StatisticsType.FILTER)
                .build();

        adminService.saveStatistics(statistics);
    }
}
