package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsFilterRes;
import com.ureca.uhyu.domain.admin.enums.StatisticsType;
import com.ureca.uhyu.domain.admin.repository.StatisticsRepository;
import com.ureca.uhyu.domain.admin.dto.response.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private AdminService adminService;

    @DisplayName("카테고리, 브랜드 별 즐겨찾기/마이맵 갯수 통계 조회 - 성공")
    @Test
    void findStatisticsBookmarkByCategoryAndBrand_success() {
        // given
        Tuple tuple1 = mock(Tuple.class);
        when(tuple1.get(0, String.class)).thenReturn("브랜드1");
        when(tuple1.get(1, Long.class)).thenReturn(1L);
        when(tuple1.get(2, String.class)).thenReturn("카테고리A");
        when(tuple1.get(4, Long.class)).thenReturn(10L);

        Tuple tuple2 = mock(Tuple.class);
        when(tuple2.get(0, String.class)).thenReturn("브랜드2");
        when(tuple2.get(1, Long.class)).thenReturn(1L);
        when(tuple2.get(2, String.class)).thenReturn("카테고리A");
        when(tuple2.get(4, Long.class)).thenReturn(20L);

        Tuple tuple3 = mock(Tuple.class);
        when(tuple3.get(0, String.class)).thenReturn("브랜드3");
        when(tuple3.get(1, Long.class)).thenReturn(2L);
        when(tuple3.get(2, String.class)).thenReturn("카테고리B");
        when(tuple3.get(4, Long.class)).thenReturn(15L);

        List<Tuple> mockTuples = List.of(tuple1, tuple2, tuple3);
        when(statisticsRepository.findBookmarkMyMapStatistics()).thenReturn(mockTuples);

        // when
        List<StatisticsBookmarkMyMapRes> result = adminService.findStatisticsBookmarkByCategoryAndBrand();

        // then
        assertEquals(2, result.size());

        StatisticsBookmarkMyMapRes catA = result.get(0);
        assertEquals("카테고리A", catA.categoryName());
        assertEquals(30, catA.sumStatisticsBookmarksByCategory());
        assertEquals(2, catA.bookmarksByBrandList().size());

        StatisticsBookmarkMyMapRes catB = result.get(1);
        assertEquals("카테고리B", catB.categoryName());
        assertEquals(15, catB.sumStatisticsBookmarksByCategory());
        assertEquals(1, catB.bookmarksByBrandList().size());

        verify(statisticsRepository).findBookmarkMyMapStatistics();
    }

    @DisplayName("카테고리, 브랜드 별 즐겨찾기/마이맵 통계 조회 - 빈 리스트")
    @Test
    void findStatisticsBookmarkByCategoryAndBrand_empty() {
        // given
        when(statisticsRepository.findBookmarkMyMapStatistics()).thenReturn(Collections.emptyList());

        // when
        List<StatisticsBookmarkMyMapRes> result = adminService.findStatisticsBookmarkByCategoryAndBrand();

        // then
        assertTrue(result.isEmpty());
        verify(statisticsRepository).findBookmarkMyMapStatistics();
    }

    @DisplayName("카테고리별 필터링 통계 조회 - 성공")
    @Test
    void findStatisticsFilterByCategory_success() {
        // given
        Tuple tuple1 = mock(Tuple.class);
        when(tuple1.get(0, Long.class)).thenReturn(1L);
        when(tuple1.get(1, String.class)).thenReturn("카페");
        when(tuple1.get(2, Long.class)).thenReturn(15L);

        Tuple tuple2 = mock(Tuple.class);
        when(tuple2.get(0, Long.class)).thenReturn(2L);
        when(tuple2.get(1, String.class)).thenReturn("패션");
        when(tuple2.get(2, Long.class)).thenReturn(8L);

        when(statisticsRepository.findFilterStatistics()).thenReturn(List.of(tuple1, tuple2));

        // when
        List<StatisticsFilterRes> result = adminService.findStatisticsFilterByCategory();

        // then
        assertEquals(2, result.size());
        assertEquals("카페", result.get(0).categoryName());
        assertEquals(15, result.get(0).sumStatisticsFilterByCategory());

        verify(statisticsRepository).findFilterStatistics();
    }

    @DisplayName("카테고리, 브랜드별 추천 통계 조회 - 성공")
    @Test
    void findStatisticsRecommendationByCategoryAndBrand_success() {
        // given
        Tuple tuple1 = mock(Tuple.class);
        when(tuple1.get(0, String.class)).thenReturn("브랜드1");
        when(tuple1.get(1, Long.class)).thenReturn(1L);
        when(tuple1.get(2, String.class)).thenReturn("카테고리A");
        when(tuple1.get(3, Long.class)).thenReturn(10L);

        Tuple tuple2 = mock(Tuple.class);
        when(tuple2.get(0, String.class)).thenReturn("브랜드2");
        when(tuple2.get(1, Long.class)).thenReturn(1L);
        when(tuple2.get(2, String.class)).thenReturn("카테고리A");
        when(tuple2.get(3, Long.class)).thenReturn(5L);

        when(statisticsRepository.findRecommendationStatistics()).thenReturn(List.of(tuple1, tuple2));

        // when
        List<StatisticsRecommendationRes> result = adminService.findStatisticsRecommendationByCategoryAndBrand();

        // then
        assertEquals(1, result.size());
        StatisticsRecommendationRes res = result.get(0);
        assertEquals("카테고리A", res.categoryName());
        assertEquals(15, res.sumStatisticsRecommendationByCategory());
        assertEquals(2, res.recommendationsByBrandList().size());

        verify(statisticsRepository).findRecommendationStatistics();
    }

    @DisplayName("카테고리, 브랜드별 추천 통계 조회 - 빈 리스트")
    @Test
    void findStatisticsRecommendationByCategoryAndBrand_empty() {
        // given
        when(statisticsRepository.findRecommendationStatistics()).thenReturn(Collections.emptyList());

        // when
        List<StatisticsRecommendationRes> result = adminService.findStatisticsRecommendationByCategoryAndBrand();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty(), "빈 리스트가 반환되어야 합니다.");

        verify(statisticsRepository).findRecommendationStatistics();
    }

    @DisplayName("카테고리, 브랜드별 멤버십 사용 통계 조회 - 성공")
    @Test
    void findStatisticsMembershipUsageByCategoryAndBrand_success() {
        // given
        Tuple tuple = mock(Tuple.class);
        when(tuple.get(0, String.class)).thenReturn("브랜드1");
        when(tuple.get(1, Long.class)).thenReturn(1L);
        when(tuple.get(2, String.class)).thenReturn("카테고리1");
        when(tuple.get(3, Long.class)).thenReturn(7L);

        when(statisticsRepository.findMembershipUsageStatistics()).thenReturn(List.of(tuple));

        // when
        List<StatisticsMembershipUsageRes> result = adminService.findStatisticsMembershipUsageByCategoryAndBrand();

        // then
        assertEquals(1, result.size());
        assertEquals("카테고리1", result.get(0).categoryName());
        assertEquals(7, result.get(0).sumStatisticsMembershipUsageByCategory());

        verify(statisticsRepository).findMembershipUsageStatistics();
    }

    @DisplayName("카테고리, 브랜드별 멤버십 사용 통계 조회 - 빈 리스트")
    @Test
    void findStatisticsMembershipUsageByCategoryAndBrand_empty() {
        // given
        when(statisticsRepository.findMembershipUsageStatistics()).thenReturn(Collections.emptyList());

        // when
        List<StatisticsMembershipUsageRes> result = adminService.findStatisticsMembershipUsageByCategoryAndBrand();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(statisticsRepository).findMembershipUsageStatistics();
    }

    @DisplayName("전체 통계 조회 - 성공")
    @Test
    void findStatisticsTotal_success() {
        // given
        when(statisticsRepository.countByStatisticsTypeIn(List.of(StatisticsType.BOOKMARK, StatisticsType.MYMAP)))
                .thenReturn(100L);
        when(statisticsRepository.countByStatisticsType(StatisticsType.FILTER))
                .thenReturn(25L);
        when(statisticsRepository.countByStatisticsType(StatisticsType.MEMBERSHIP_USAGE))
                .thenReturn(55L);

        // when
        StatisticsTotalRes result = adminService.findStatisticsTotal();

        // then
        assertEquals(100L, result.totalBookmarkMyMap());
        assertEquals(25L, result.totalFiltering());
        assertEquals(55L, result.totalMembershipUsage());

        verify(statisticsRepository).countByStatisticsTypeIn(List.of(StatisticsType.BOOKMARK, StatisticsType.MYMAP));
        verify(statisticsRepository).countByStatisticsType(StatisticsType.FILTER);
        verify(statisticsRepository).countByStatisticsType(StatisticsType.MEMBERSHIP_USAGE);
    }

    @DisplayName("전체 통계 조회 - db에 값이 든게 없을경우")
    @Test
    void findStatisticsTotal_emptyDataset() {
        // given
        when(statisticsRepository.countByStatisticsTypeIn(List.of(StatisticsType.BOOKMARK, StatisticsType.MYMAP))).thenReturn(0L);
        when(statisticsRepository.countByStatisticsType(StatisticsType.FILTER)).thenReturn(0L);
        when(statisticsRepository.countByStatisticsType(StatisticsType.MEMBERSHIP_USAGE)).thenReturn(0L);

        // when
        StatisticsTotalRes result = adminService.findStatisticsTotal();

        // then
        assertEquals(0L, result.totalBookmarkMyMap());
        assertEquals(0L, result.totalFiltering());
        assertEquals(0L, result.totalMembershipUsage());

        verify(statisticsRepository).countByStatisticsTypeIn(List.of(StatisticsType.BOOKMARK, StatisticsType.MYMAP));
        verify(statisticsRepository).countByStatisticsType(StatisticsType.FILTER);
        verify(statisticsRepository).countByStatisticsType(StatisticsType.MEMBERSHIP_USAGE);
    }
}