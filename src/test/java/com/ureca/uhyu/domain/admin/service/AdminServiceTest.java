package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsFilterRes;
import com.ureca.uhyu.domain.admin.dto.response.UserBrandPair;
import com.ureca.uhyu.domain.user.enums.ActionType;
import com.ureca.uhyu.domain.user.repository.actionLogs.ActionLogsRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import com.ureca.uhyu.domain.admin.dto.response.*;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.user.repository.history.HistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private ActionLogsRepository actionLogsRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private AdminService adminService;

    @DisplayName("카테고리, 브랜드 별 즐겨찾기 갯수 통계 조회 - 성공")
    @Test
    void findStatisticsBookmarkByCategoryAndBrand() {
        // given
        List<StatisticsBookmarkRes> mockResList = List.of(
                StatisticsBookmarkRes.of(
                        1L,
                        "카테고리A",
                        30,
                        new ArrayList<>(List.of(
                                BookmarksByBrand.of("브랜드1", 10),
                                BookmarksByBrand.of("브랜드2", 20)
                        ))
                ),
                StatisticsBookmarkRes.of(
                        2L,
                        "카테고리B",
                        15,
                        new ArrayList<>(List.of(
                                BookmarksByBrand.of("브랜드3", 15)
                        ))
                )
        );

        when(bookmarkRepository.findStatisticsBookmarkByCategoryAndBrand()).thenReturn(mockResList);

        // when
        List<StatisticsBookmarkRes> result = adminService.findStatisticsBookmarkByCategoryAndBrand();

        // then
        assertEquals(2, result.size());
        assertEquals("카테고리A", result.get(0).categoryName());
        assertEquals(30, result.get(0).sumStatisticsBookmarksByCategory());
        assertEquals(2, result.get(0).bookmarksByBrandList().size());
        assertEquals("브랜드1", result.get(0).bookmarksByBrandList().get(0).brandName());

        assertEquals("카테고리B", result.get(1).categoryName());
        assertEquals(15, result.get(1).sumStatisticsBookmarksByCategory());
        assertEquals(1, result.get(1).bookmarksByBrandList().size());
        assertEquals("브랜드3", result.get(1).bookmarksByBrandList().get(0).brandName());
        verify(bookmarkRepository).findStatisticsBookmarkByCategoryAndBrand();
    }
    
    @DisplayName("카테고리, 브랜드 별 즐겨찾기 갯수 통계 조회 - 빈 리스트")
    @Test
    void findStatisticsBookmarkByCategoryAndBrand_EmptyDataset() {
        // given
        when(bookmarkRepository.findStatisticsBookmarkByCategoryAndBrand()).thenReturn(List.of());

        // when
        List<StatisticsBookmarkRes> result = adminService.findStatisticsBookmarkByCategoryAndBrand();

        // then
        assertTrue(result.isEmpty());
        verify(bookmarkRepository).findStatisticsBookmarkByCategoryAndBrand();
    }

    @DisplayName("카테고리별 필터링 횟수 통계 조회 - 성공")
    @Test
    void findStatisticsFilterByCategory() {
        // given
        StatisticsFilterRes res1 = new StatisticsFilterRes(1L, "카페", 15);
        StatisticsFilterRes res2 = new StatisticsFilterRes(2L, "패션", 8);
        List<StatisticsFilterRes> mockResult = List.of(res1, res2);

        when(actionLogsRepository.findStatisticsFilterByActionType(ActionType.FILTER_USED))
                .thenReturn(mockResult);

        // when
        List<StatisticsFilterRes> result = adminService.findStatisticsFilterByCategory();

        // then
        assertEquals(2, result.size());

        StatisticsFilterRes first = result.get(0);
        assertEquals(1L, first.categoryId());
        assertEquals("카페", first.categoryName());
        assertEquals(15, first.sumStatisticsFilterByCategory());

        StatisticsFilterRes second = result.get(1);
        assertEquals(2L, second.categoryId());
        assertEquals("패션", second.categoryName());
        assertEquals(8, second.sumStatisticsFilterByCategory());

        verify(actionLogsRepository).findStatisticsFilterByActionType(ActionType.FILTER_USED);
    }

    @DisplayName("카테고리, 브랜드별 추천 통계 조회 - 성공")
    @Test
    void findStatisticsRecommendationByCategoryAndBrand_success() {
        // given
        RecommendationsByBrand brand1 = RecommendationsByBrand.of("스타벅스", 12);
        RecommendationsByBrand brand2 = RecommendationsByBrand.of("이디야", 8);
        StatisticsRecommendationRes category1 = StatisticsRecommendationRes.of(1L, "카페", 20, List.of(brand1, brand2));

        RecommendationsByBrand brand3 = RecommendationsByBrand.of("무신사", 6);
        StatisticsRecommendationRes category2 = StatisticsRecommendationRes.of(2L, "패션", 6, List.of(brand3));

        List<StatisticsRecommendationRes> mockResult = List.of(category1, category2);

        when(recommendationRepository.findStatisticsRecommendationByCategory()).thenReturn(mockResult);

        // when
        List<StatisticsRecommendationRes> result = adminService.findStatisticsRecommendationByCategoryAndBrand();

        // then
        assertEquals(2, result.size());

        StatisticsRecommendationRes res1 = result.get(0);
        assertEquals(1L, res1.categoryId());
        assertEquals("카페", res1.categoryName());
        assertEquals(20, res1.sumStatisticsRecommendationByCategory());
        assertEquals(2, res1.recommendationsByBrandList().size());
        assertEquals("스타벅스", res1.recommendationsByBrandList().get(0).brandName());
        assertEquals(12, res1.recommendationsByBrandList().get(0).sumRecommendationsByBrand());

        StatisticsRecommendationRes res2 = result.get(1);
        assertEquals(2L, res2.categoryId());
        assertEquals("패션", res2.categoryName());
        assertEquals(6, res2.sumStatisticsRecommendationByCategory());
        assertEquals(1, res2.recommendationsByBrandList().size());
        assertEquals("무신사", res2.recommendationsByBrandList().get(0).brandName());
        assertEquals(6, res2.recommendationsByBrandList().get(0).sumRecommendationsByBrand());

        verify(recommendationRepository).findStatisticsRecommendationByCategory();
    }

    @DisplayName("카테고리, 브랜드별 추천 통계 조회 - 빈 리스트")
    @Test
    void findStatisticsRecommendationByCategoryAndBrand_emptyDataset() {
        // given
        when(recommendationRepository.findStatisticsRecommendationByCategory()).thenReturn(Collections.emptyList());

        // when
        List<StatisticsRecommendationRes> result = adminService.findStatisticsRecommendationByCategoryAndBrand();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty(), "빈 리스트가 반환되어야 합니다.");

        verify(recommendationRepository).findStatisticsRecommendationByCategory();
    }

    @DisplayName("카테고리, 브랜드별 멤버십 사용횟수 통계 조회 - 성공")
    @Test
    void findStatisticsMembershipUsageByCategoryAndBrand_success() {
        // given
        List<MembershipUsageByBrand> usageList1 = List.of(
                MembershipUsageByBrand.of("브랜드A", 5),
                MembershipUsageByBrand.of("브랜드B", 3)
        );

        List<MembershipUsageByBrand> usageList2 = List.of(
                MembershipUsageByBrand.of("브랜드C", 2)
        );

        List<StatisticsMembershipUsageRes> expected = List.of(
                StatisticsMembershipUsageRes.of(1L, "카테고리1", 8, usageList1),
                StatisticsMembershipUsageRes.of(2L, "카테고리2", 2, usageList2)
        );

        when(historyRepository.findStatisticsMembershipUsageByCategory()).thenReturn(expected);

        // when
        List<StatisticsMembershipUsageRes> result = adminService.findStatisticsMembershipUsageByCategoryAndBrand();

        // then
        assertEquals(2, result.size());
        assertEquals("카테고리1", result.get(0).categoryName());
        assertEquals(8, result.get(0).sumStatisticsMembershipUsageByCategory());
        assertEquals("브랜드A", result.get(0).membershipUsageByBrandList().get(0).brandName());
        verify(historyRepository).findStatisticsMembershipUsageByCategory();
    }

    @DisplayName("카테고리, 브랜드별 멤버십 사용횟수 통계 조회 - 빈 리스트")
    @Test
    void findStatisticsMembershipUsageByCategoryAndBrand_emptyDataset() {
        // given
        when(historyRepository.findStatisticsMembershipUsageByCategory()).thenReturn(List.of());

        // when
        List<StatisticsMembershipUsageRes> result = adminService.findStatisticsMembershipUsageByCategoryAndBrand();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(historyRepository).findStatisticsMembershipUsageByCategory();
    }

    @DisplayName("전체 통계 조회 - 성공")
    @Test
    void findStatisticsTotal_success() {
        // given
        Long bookmarkCount = 100L;
        Long filterCount = 25L;
        Long historyCount = 55L;

        when(bookmarkRepository.count()).thenReturn(bookmarkCount);
        when(actionLogsRepository.countByActionType(ActionType.FILTER_USED)).thenReturn(filterCount);
        when(historyRepository.count()).thenReturn(historyCount);

        // when
        StatisticsTotalRes result = adminService.findStatisticsTotal();

        // then
        assertEquals(bookmarkCount, result.totalBookmark());
        assertEquals(filterCount, result.totalFiltering());
        assertEquals(historyCount, result.totalMembershipUsage());

        verify(bookmarkRepository).count();
        verify(actionLogsRepository).countByActionType(ActionType.FILTER_USED);
        verify(historyRepository).count();
    }

    @DisplayName("전체 통계 조회 - db에 값이 든게 없을경우")
    @Test
    void findStatisticsTotal_emptyDataset() {
        // given
        when(bookmarkRepository.count()).thenReturn(0L);
        when(actionLogsRepository.countByActionType(ActionType.FILTER_USED)).thenReturn(0L);
        when(historyRepository.count()).thenReturn(0L);

        // when
        StatisticsTotalRes result = adminService.findStatisticsTotal();

        // then
        assertEquals(0L, result.totalBookmark());
        assertEquals(0L, result.totalFiltering());
        assertEquals(0L, result.totalMembershipUsage());

        verify(bookmarkRepository).count();
        verify(actionLogsRepository).countByActionType(ActionType.FILTER_USED);
        verify(historyRepository).count();
    }
}