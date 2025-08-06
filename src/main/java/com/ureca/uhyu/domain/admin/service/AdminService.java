package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.StatisticsDto;
import com.ureca.uhyu.domain.admin.dto.response.*;
import com.ureca.uhyu.domain.admin.entity.Statistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import com.ureca.uhyu.domain.admin.repository.StatisticsRepository;
import com.ureca.uhyu.domain.mymap.repository.MyMapRepository;
import com.ureca.uhyu.domain.user.repository.actionLogs.ActionLogsRepository;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import com.ureca.uhyu.domain.user.repository.history.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

        List<StatisticsDto> brandStats = tuples.stream()
                .map(t -> new StatisticsDto(
                        t.get(0, String.class), // brandName
                        t.get(1, Long.class),   // categoryId
                        t.get(2, String.class), // categoryName
                        t.get(4, Long.class).intValue() // sum
                ))
                .toList();

        //카테고리별 그룹핑
        Map<Long, List<StatisticsDto>> grouped = brandStats.stream()
                .collect(Collectors.groupingBy(StatisticsDto::categoryId));

        // Step 3: 최종 DTO 매핑
        List<StatisticsBookmarkMyMapRes> statisticsBookmarkMyMapResList = new ArrayList<>();
        for (Map.Entry<Long, List<StatisticsDto>> entry : grouped.entrySet()) {
            Long categoryId = entry.getKey();
            List<StatisticsDto> brandList = entry.getValue();
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
        log.debug("Bookmark 쿼리 실행 시간: " + elapsedMs + " ms");

        //return statisticsBookmarkMyMapList.stream().map(StatisticsBookmarkRes::from).toList();
        return statisticsBookmarkMyMapResList;
    }

    public List<StatisticsFilterRes> findStatisticsFilterByCategory() {

        long start = System.nanoTime();

        //List<StatisticsFilterRes> statisticsFilterRes = actionLogsRepository.findStatisticsFilterByActionType(ActionType.FILTER_USED);
        List<Tuple> tuples = statisticsRepository.findFilterStatistics();

        List<StatisticsFilterRes> statisticsFilterRes = tuples.stream()
                .map(t -> StatisticsFilterRes.of(
                        t.get(0, Long.class),              // categoryId
                        t.get(1, String.class),            // categoryName
                        t.get(2, Long.class).intValue()    // count
                ))
                .toList();
        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        log.debug("filtering 쿼리 실행 시간: " + elapsedMs + " ms");

        return statisticsFilterRes;
    }

    public List<StatisticsRecommendationRes> findStatisticsRecommendationByCategoryAndBrand() {

        long start = System.nanoTime();
        //List<StatisticsRecommendationRes> statisticsRecommendationRes = recommendationRepository.findStatisticsRecommendationByCategory();
        List<Tuple> tuples = statisticsRepository.findRecommendationStatistics();

        // Step 1: 중간 객체로 변환
        List<StatisticsDto> rawList = tuples.stream()
                .map(t -> new StatisticsDto(
                        t.get(0, String.class),         // brandName
                        t.get(1, Long.class),           // categoryId
                        t.get(2, String.class),         // categoryName
                        t.get(3, Long.class).intValue() // count
                ))
                .toList();

        // Step 2: 카테고리 기준 그룹핑
        Map<Long, List<StatisticsDto>> grouped = rawList.stream()
                .collect(Collectors.groupingBy(StatisticsDto::categoryId));

        // Step 3: DTO로 매핑
        List<StatisticsRecommendationRes> statisticsRecommendationResList = new ArrayList<>();

        for (Map.Entry<Long, List<StatisticsDto>> entry : grouped.entrySet()) {
            Long categoryId = entry.getKey();
            List<StatisticsDto> groupList = entry.getValue();
            String categoryName = groupList.get(0).categoryName(); // 동일하므로

            List<RecommendationsByBrand> brandList = groupList.stream()
                    .map(b -> RecommendationsByBrand.of(b.brandName(), b.count()))
                    .toList();

            int total = brandList.stream().mapToInt(RecommendationsByBrand::sumRecommendationsByBrand).sum();

            statisticsRecommendationResList.add(StatisticsRecommendationRes.of(
                    categoryId,
                    categoryName,
                    total,
                    brandList
            ));
        }

        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        log.debug("recommendation 쿼리 실행 시간: " + elapsedMs + " ms");

        return statisticsRecommendationResList;
    }

    public List<StatisticsMembershipUsageRes> findStatisticsMembershipUsageByCategoryAndBrand() {

        long start = System.nanoTime();
//        List<StatisticsMembershipUsageRes> statisticsMembershipUsageResList = historyRepository.findStatisticsMembershipUsageByCategory();
        List<Tuple> tuples = statisticsRepository.findMembershipUsageStatistics();

        // Step 1: 튜플 → 중간 DTO로 변환
        List<StatisticsDto> rawList = tuples.stream()
                .map(t -> new StatisticsDto(
                        t.get(0, String.class),         // brandName
                        t.get(1, Long.class),           // categoryId
                        t.get(2, String.class),         // categoryName
                        t.get(3, Long.class).intValue() // count
                ))
                .toList();

        // Step 2: 카테고리 기준 그룹핑
        Map<Long, List<StatisticsDto>> grouped = rawList.stream()
                .collect(Collectors.groupingBy(StatisticsDto::categoryId));

        // Step 3: 최종 DTO 매핑
        List<StatisticsMembershipUsageRes> statisticsMembershipUsageResList = new ArrayList<>();

        for (Map.Entry<Long, List<StatisticsDto>> entry : grouped.entrySet()) {
            Long categoryId = entry.getKey();
            List<StatisticsDto> brandGroup = entry.getValue();
            String categoryName = brandGroup.get(0).categoryName(); // 동일하므로

            List<MembershipUsageByBrand> brandDtos = brandGroup.stream()
                    .map(b -> MembershipUsageByBrand.of(b.brandName(), b.count()))
                    .toList();

            int total = brandDtos.stream()
                    .mapToInt(MembershipUsageByBrand::sumMembershipUsageByBrand)
                    .sum();

            statisticsMembershipUsageResList.add(StatisticsMembershipUsageRes.of(
                    categoryId,
                    categoryName,
                    total,
                    brandDtos
            ));
        }
        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        log.debug("membership usage 쿼리 실행 시간: " + elapsedMs + " ms");

        return statisticsMembershipUsageResList;
    }

    public StatisticsTotalRes findStatisticsTotal() {

        long start = System.nanoTime();

        Long totalBookmarkMyMap = statisticsRepository.countByStatisticsTypeIn(List.of(StatisticsType.BOOKMARK, StatisticsType.MYMAP));
        Long totalFiltering = statisticsRepository.countByStatisticsType(StatisticsType.FILTER);
        Long totalMembershipUsage = statisticsRepository.countByStatisticsType(StatisticsType.MEMBERSHIP_USAGE);

        long end = System.nanoTime();
        double elapsedMs = (end - start) / 1_000_000.0;
        log.debug("전체 통계 쿼리 실행 시간: " + elapsedMs + " ms");

        return StatisticsTotalRes.of(totalBookmarkMyMap, totalFiltering, totalMembershipUsage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveStatistics(Statistics statistics) {
        statisticsRepository.save(statistics);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteStatisticsMyMapType(Long userId, Long storeId, Long myMapListId, StatisticsType statisticsType) {
        statisticsRepository
                .deleteByUserIdAndStoreIdAndMyMapListIdAndStatisticsType(userId, storeId, myMapListId, statisticsType);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteStatisticsBookmarkType(Long userId, Long storeId, StatisticsType statisticsType) {
        statisticsRepository
                .deleteByUserIdAndStoreIdAndStatisticsType(userId, storeId, statisticsType);
    }
}
