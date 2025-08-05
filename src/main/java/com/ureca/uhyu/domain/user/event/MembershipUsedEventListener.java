package com.ureca.uhyu.domain.user.event;

import com.ureca.uhyu.domain.admin.entity.Statistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import com.ureca.uhyu.domain.admin.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MembershipUsedEventListener {

    private final StatisticsRepository statisticsRepository;

    @EventListener
    public void handleMembershipUsedEvent(MembershipUsedEvent event) {
        Statistics statistics = Statistics.builder()
                .userId(event.getUserId())
                .storeId(event.getStoreId())
                .brandId(event.getBrandId())
                .brandName(event.getBrandName())
                .categoryId(event.getCategoryId())
                .categoryName(event.getCategoryName())
                .statisticsType(StatisticsType.MEMBERSHIP_USAGE)
                .build();

        statisticsRepository.save(statistics);
    }
}
