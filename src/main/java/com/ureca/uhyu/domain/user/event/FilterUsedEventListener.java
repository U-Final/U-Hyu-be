package com.ureca.uhyu.domain.user.event;

import com.ureca.uhyu.domain.admin.entity.Statistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import com.ureca.uhyu.domain.admin.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterUsedEventListener {

    private final StatisticsRepository statisticsRepository;

    @EventListener
    public void handleFilterUsedEvent(FilterUsedEvent event) {
        Statistics statistics = Statistics.builder()
                .userId(event.getUserId())
                .categoryId(event.getCategoryId())
                .categoryName(event.getCategoryName())
                .statisticsType(StatisticsType.FILTER)
                .build();

        statisticsRepository.save(statistics);
    }
}
