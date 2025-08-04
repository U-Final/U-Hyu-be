package com.ureca.uhyu.domain.mymap.event;

import com.ureca.uhyu.domain.admin.entity.Statistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import com.ureca.uhyu.domain.admin.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MyMapEventListener {

    private final StatisticsRepository statisticsRepository;

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
            statisticsRepository.save(statistics);
        }
        else if (event.getAction() == MyMapToggledEvent.Action.REMOVE) {
            statisticsRepository.deleteByUserIdAndStoreIdAndMyMapListIdAndStatisticsType(
                    event.getUserId(),
                    event.getStoreId(),
                    event.getMyMapListId(),
                    StatisticsType.MYMAP
            );
        }
    }
}
