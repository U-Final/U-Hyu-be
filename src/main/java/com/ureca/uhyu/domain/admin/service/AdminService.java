package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsDto;
import com.ureca.uhyu.domain.admin.dto.response.*;
import com.ureca.uhyu.domain.admin.entity.Statistics;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import com.ureca.uhyu.domain.admin.repository.StatisticsRepository;
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

    private final StatisticsRepository statisticsRepository;

    public List<StatisticsBookmarkMyMapRes> findStatisticsBookmarkByCategoryAndBrand() {

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

        return statisticsBookmarkMyMapResList;
    }

    public List<StatisticsFilterRes> findStatisticsFilterByCategory() {

        List<Tuple> tuples = statisticsRepository.findFilterStatistics();

        List<StatisticsFilterRes> statisticsFilterRes = tuples.stream()
                .map(t -> StatisticsFilterRes.of(
                        t.get(0, Long.class),              // categoryId
                        t.get(1, String.class),            // categoryName
                        t.get(2, Long.class).intValue()    // count
                ))
                .toList();

        return statisticsFilterRes;
    }

    public List<StatisticsRecommendationRes> findStatisticsRecommendationByCategoryAndBrand() {

        List<Tuple> tuples = statisticsRepository.findRecommendationStatistics();

        List<StatisticsDto> rawList = tuples.stream()
                .map(t -> new StatisticsDto(
                        t.get(0, String.class),         // brandName
                        t.get(1, Long.class),           // categoryId
                        t.get(2, String.class),         // categoryName
                        t.get(3, Long.class).intValue() // count
                ))
                .toList();

        // 카테고리 기준 그룹핑
        Map<Long, List<StatisticsDto>> grouped = rawList.stream()
                .collect(Collectors.groupingBy(StatisticsDto::categoryId));

        List<StatisticsRecommendationRes> statisticsRecommendationResList = new ArrayList<>();

        for (Map.Entry<Long, List<StatisticsDto>> entry : grouped.entrySet()) {
            Long categoryId = entry.getKey();
            List<StatisticsDto> groupList = entry.getValue();
            String categoryName = groupList.get(0).categoryName();

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

        return statisticsRecommendationResList;
    }

    public List<StatisticsMembershipUsageRes> findStatisticsMembershipUsageByCategoryAndBrand() {

        List<Tuple> tuples = statisticsRepository.findMembershipUsageStatistics();

        List<StatisticsDto> rawList = tuples.stream()
                .map(t -> new StatisticsDto(
                        t.get(0, String.class),         // brandName
                        t.get(1, Long.class),           // categoryId
                        t.get(2, String.class),         // categoryName
                        t.get(3, Long.class).intValue() // count
                ))
                .toList();

        // 카테고리 기준 그룹핑
        Map<Long, List<StatisticsDto>> grouped = rawList.stream()
                .collect(Collectors.groupingBy(StatisticsDto::categoryId));

        List<StatisticsMembershipUsageRes> statisticsMembershipUsageResList = new ArrayList<>();

        for (Map.Entry<Long, List<StatisticsDto>> entry : grouped.entrySet()) {
            Long categoryId = entry.getKey();
            List<StatisticsDto> brandGroup = entry.getValue();
            String categoryName = brandGroup.get(0).categoryName();

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

        return statisticsMembershipUsageResList;
    }

    public StatisticsTotalRes findStatisticsTotal() {

        Long totalBookmarkMyMap = statisticsRepository.countByStatisticsTypeIn(List.of(StatisticsType.BOOKMARK, StatisticsType.MYMAP));
        Long totalFiltering = statisticsRepository.countByStatisticsType(StatisticsType.FILTER);
        Long totalMembershipUsage = statisticsRepository.countByStatisticsType(StatisticsType.MEMBERSHIP_USAGE);

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
