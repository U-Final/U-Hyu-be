package com.ureca.uhyu.domain.map.event;

import com.ureca.uhyu.domain.admin.entity.Statistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import com.ureca.uhyu.domain.admin.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookmarkEventListener {

    private final StatisticsRepository statisticsRepository;

    @EventListener
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
            statisticsRepository.save(statistics);

        } else if (event.getAction() == BookmarkToggledEvent.Action.REMOVE) {
            statisticsRepository.deleteByUserIdAndStoreIdAndStatisticsType(
                    event.getUserId(),
                    event.getStoreId(),
                    StatisticsType.BOOKMARK
            );
        }
    }
}
