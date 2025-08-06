package com.ureca.uhyu.domain.map.event;

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
public class BookmarkEventListener {

    private final AdminService adminService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookmarkToggled(BookmarkToggledEvent event) {
        if (event.getAction() == BookmarkToggledEvent.Action.ADD) {
            Statistics statistics = Statistics.builder()
                    .userId(event.getUserId())
                    .storeId(event.getStoreId())
                    .brandId(event.getBrandId())
                    .brandName(event.getBrandName())
                    .categoryId(event.getCategoryId())
                    .categoryName(event.getCategoryName())
                    .statisticsType(StatisticsType.BOOKMARK)
                    .build();
            adminService.saveStatistics(statistics);

        } else if (event.getAction() == BookmarkToggledEvent.Action.REMOVE) {
            adminService.deleteStatisticsBookmarkType(
                    event.getUserId(),
                    event.getStoreId(),
                    StatisticsType.BOOKMARK
            );
        }
    }
}
