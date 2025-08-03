package com.ureca.uhyu.domain.admin.service;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final BookmarkRepository bookmarkRepository;
    private final ActionLogsRepository actionLogsRepository;
    private final RecommendationRepository recommendationRepository;
    private final HistoryRepository historyRepository;

    public List<StatisticsBookmarkRes> findStatisticsBookmarkByCategoryAndBrand() {

        long start = System.nanoTime();
        List<StatisticsBookmarkRes> statisticsBookmarkResList = bookmarkRepository.findStatisticsBookmarkByCategoryAndBrand();
        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        System.out.println("Bookmark 쿼리 실행 시간: " + elapsedMs + " ms");

        return statisticsBookmarkResList;
    }

    public List<StatisticsFilterRes> findStatisticsFilterByCategory() {

        long start = System.nanoTime();
        List<StatisticsFilterRes> statisticsFilterRes = actionLogsRepository.findStatisticsFilterByActionType(ActionType.FILTER_USED);
        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        System.out.println("filtering 쿼리 실행 시간: " + elapsedMs + " ms");

        return statisticsFilterRes;
    }

    public List<StatisticsRecommendationRes> findStatisticsRecommendationByCategoryAndBrand() {

        long start = System.nanoTime();
        List<StatisticsRecommendationRes> statisticsRecommendationRes = recommendationRepository.findStatisticsRecommendationByCategory();
        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        System.out.println("recommendation 쿼리 실행 시간: " + elapsedMs + " ms");

        return statisticsRecommendationRes;
    }

    public List<StatisticsMembershipUsageRes> findStatisticsMembershipUsageByCategoryAndBrand() {

        long start = System.nanoTime();
        List<StatisticsMembershipUsageRes> statisticsMembershipUsageRes = historyRepository.findStatisticsMembershipUsageByCategory();
        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        System.out.println("membership usage 쿼리 실행 시간: " + elapsedMs + " ms");

        return statisticsMembershipUsageRes;
    }

    public StatisticsTotalRes findStatisticsTotal() {
        Long totalBookmark = bookmarkRepository.count();
        Long totalFiltering = actionLogsRepository.countByActionType(ActionType.FILTER_USED);
        Long totalMembershipUsage = historyRepository.count();

        return StatisticsTotalRes.of(totalBookmark, totalFiltering, totalMembershipUsage);
    }
}
