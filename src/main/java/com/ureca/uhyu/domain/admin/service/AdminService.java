package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.StatisticsBookmarkMyMap;
import com.ureca.uhyu.domain.admin.dto.response.*;
import com.ureca.uhyu.domain.admin.repository.StatisticsRepository;
import com.ureca.uhyu.domain.mymap.repository.MyMapRepository;
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

    private final BookmarkRepository bookmarkRepository;
    private final ActionLogsRepository actionLogsRepository;
    private final RecommendationRepository recommendationRepository;
    private final HistoryRepository historyRepository;
    private final MyMapRepository myMapRepository;
    private final StatisticsRepository statisticsRepository;

    public List<StatisticsBookmarkMyMapRes> findStatisticsBookmarkByCategoryAndBrand() {

        long start = System.nanoTime();

        //List<StatisticsBookmarkMyMapRes> statisticsBookmarkMyMapResList = bookmarkRepository.findStatisticsBookmarkByCategoryAndBrand();
        List<Tuple> tuples = statisticsRepository.findBookmarkMyMapStatistics();

        List<StatisticsBookmarkMyMap> brandStats = tuples.stream()
                .map(t -> new StatisticsBookmarkMyMap(
                        t.get(0, String.class), // brandName
                        t.get(1, Long.class),   // categoryId
                        t.get(2, String.class), // categoryName
                        t.get(4, Long.class).intValue() // sum
                ))
                .toList();

        //카테고리별 그룹핑
        Map<Long, List<StatisticsBookmarkMyMap>> grouped = brandStats.stream()
                .collect(Collectors.groupingBy(StatisticsBookmarkMyMap::categoryId));

        // Step 3: 최종 DTO 매핑
        List<StatisticsBookmarkMyMapRes> statisticsBookmarkMyMapResList = new ArrayList<>();
        for (Map.Entry<Long, List<StatisticsBookmarkMyMap>> entry : grouped.entrySet()) {
            Long categoryId = entry.getKey();
            List<StatisticsBookmarkMyMap> brandList = entry.getValue();
            String categoryName = brandList.get(0).categoryName();

            List<BookmarksByBrand> brandDtos = brandList.stream()
                    .map(b -> BookmarksByBrand.of(b.brandName(), b.count()))
                    .toList();

            int total = brandDtos.stream().mapToInt(BookmarksByBrand::sumBookmarksByBrand).sum();

            statisticsBookmarkMyMapResList.add(StatisticsBookmarkMyMapRes.of(
                    categoryId,
                    categoryName,
                    total,
                    brandDtos
            ));
        }

        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        System.out.println("Bookmark 쿼리 실행 시간: " + elapsedMs + " ms");

        //return statisticsBookmarkMyMapList.stream().map(StatisticsBookmarkRes::from).toList();
        return statisticsBookmarkMyMapResList;
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
        Long totalBookmarkMyMap = bookmarkRepository.count() + myMapRepository.count();
        Long totalFiltering = actionLogsRepository.countByActionType(ActionType.FILTER_USED);
        Long totalMembershipUsage = historyRepository.count();

        return StatisticsTotalRes.of(totalBookmarkMyMap, totalFiltering, totalMembershipUsage);
    }
}
