package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.*;
import com.ureca.uhyu.domain.user.repository.actionLogs.ActionLogsRepository;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import com.ureca.uhyu.domain.user.enums.ActionType;
import com.ureca.uhyu.domain.user.repository.history.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private static final int CATEGORY_ID_INDEX = 1;
    private static final int CATEGORY_NAME_INDEX = 2;
    private static final int BRAND_NAME_INDEX = 3;

    private final BookmarkRepository bookmarkRepository;
    private final ActionLogsRepository actionLogsRepository;
    private final RecommendationRepository recommendationRepository;
    private final HistoryRepository historyRepository;

    public List<StatisticsBookmarkRes> findStatisticsBookmarkByCategoryAndBrand() {
        return bookmarkRepository.findStatisticsBookmarkByCategoryAndBrand();
    }

    public List<StatisticsFilterRes> findStatisticsFilterByCategory() {
        return actionLogsRepository.findStatisticsFilterByActionType(ActionType.FILTER_USED);
    }

    public List<StatisticsRecommendationRes> findStatisticsRecommendationByCategoryAndBrand() {
        return recommendationRepository.findStatisticsRecommendationByCategory();
    }

    public List<StatisticsMembershipUsageRes> findStatisticsMembershipUsageByCategoryAndBrand() {
        return historyRepository.findStatisticsMembershipUsageByCategory();
    }

    public StatisticsTotalRes findStatisticsTotal() {
        Long totalBookmark = bookmarkRepository.count();
        Long totalFiltering = actionLogsRepository.countByActionType(ActionType.FILTER_USED);
        Long totalMembershipUsage = historyRepository.count();

        return StatisticsTotalRes.of(totalBookmark, totalFiltering, totalMembershipUsage);
    }
}
