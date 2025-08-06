package com.ureca.uhyu.domain.mymap.event;

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
public class MyMapEventListener {

    private final AdminService adminService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMyMapToggled(MyMapToggledEvent event) {
        if (event.getAction() == MyMapToggledEvent.Action.ADD) {
            Statistics statistics = Statistics.builder()
                    .userId(event.getUserId())
                    .storeId(event.getStoreId())
                    .myMapListId(event.getMyMapListId())
                    .brandId(event.getBrandId())
                    .brandName(event.getBrandName())
                    .categoryId(event.getCategoryId())
                    .categoryName(event.getCategoryName())
                    .statisticsType(StatisticsType.MYMAP)
                    .build();
            adminService.saveStatistics(statistics);
        }
        else if (event.getAction() == MyMapToggledEvent.Action.REMOVE) {
            adminService.deleteStatisticsMyMapType(
                    event.getUserId(),
                    event.getStoreId(),
                    event.getMyMapListId(),
                    StatisticsType.MYMAP
            );
        }
    }
}
