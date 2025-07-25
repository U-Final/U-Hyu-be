package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.CountFilterByCategoryRes;
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
    void findBookmarksByCategoryAndBrand() {
        // given
        UserBrandPair pair1 = new UserBrandPair(1L, 101L); // 유저1, 브랜드101
        UserBrandPair pair2 = new UserBrandPair(2L, 101L); // 유저2, 브랜드101
        UserBrandPair pair3 = new UserBrandPair(3L, 102L); // 유저3, 브랜드102

        Set<UserBrandPair> userBrandSaves = Set.of(pair1, pair2, pair3);

        // Mock 튜플 (brandId → categoryId, categoryName, brandName)
        Tuple tuple1 = mock(Tuple.class);
        when(tuple1.get(1, Long.class)).thenReturn(10L);              // categoryId
        when(tuple1.get(2, String.class)).thenReturn("카테고리A");     // categoryName
        when(tuple1.get(3, String.class)).thenReturn("브랜드101");     // brandName

        Tuple tuple2 = mock(Tuple.class);
        when(tuple2.get(1, Long.class)).thenReturn(10L);
        when(tuple2.get(2, String.class)).thenReturn("카테고리A");
        when(tuple2.get(3, String.class)).thenReturn("브랜드102");

        Map<Long, Tuple> brandCategoryMap = new HashMap<>();
        brandCategoryMap.put(101L, tuple1);
        brandCategoryMap.put(102L, tuple2);

        when(bookmarkRepository.findUserBrandSaves()).thenReturn(userBrandSaves);
        when(bookmarkRepository.findBrandToCategoryMap()).thenReturn(brandCategoryMap);

        // when
        List<CountBookmarkRes> result = adminService.findBookmarksByCategoryAndBrand();

        // then
        assertEquals(1, result.size());
        CountBookmarkRes categoryRes = result.get(0);
        assertEquals(10L, categoryRes.categoryId());
        assertEquals("카테고리A", categoryRes.categoryName());
        assertEquals(3, categoryRes.sumBookmarksByCategory()); // 2 + 1

        List<BookmarksByBrand> brandList = categoryRes.bookmarksByBrandList();
        assertEquals(2, brandList.size());

        Map<String, Integer> brandCountMap = brandList.stream()
                .collect(Collectors.toMap(BookmarksByBrand::brandName, BookmarksByBrand::sumBookmarksByBrand));
        assertEquals(2, brandCountMap.get("브랜드101"));
        assertEquals(1, brandCountMap.get("브랜드102"));

        verify(bookmarkRepository).findUserBrandSaves();
        verify(bookmarkRepository).findBrandToCategoryMap();
    }
    
    @DisplayName("카테고리, 브랜드 별 즐겨찾기 갯수 통계 조회 - 빈 리스트")
    @Test
    void findBookmarksByCategoryAndBrand_EmptyDataset() {
        // given
        when(bookmarkRepository.findUserBrandSaves()).thenReturn(Set.of());
        when(bookmarkRepository.findBrandToCategoryMap()).thenReturn(Map.of());

        // when
        List<CountBookmarkRes> result = adminService.findBookmarksByCategoryAndBrand();

        // then
        assertTrue(result.isEmpty());
    }

    @DisplayName("카테고리별 필터링 횟수 통계 조회 - 성공")
    @Test
    void findCountFilterByCategory() {
        // given
        CountFilterByCategoryRes res1 = new CountFilterByCategoryRes(1L, "카페", 15);
        CountFilterByCategoryRes res2 = new CountFilterByCategoryRes(2L, "패션", 8);
        List<CountFilterByCategoryRes> mockResult = List.of(res1, res2);

        when(actionLogsRepository.findCountFilterByActionType(ActionType.FILTER_USED))
                .thenReturn(mockResult);

        // when
        List<CountFilterByCategoryRes> result = adminService.findCountFilterByCategory();

        // then
        assertEquals(2, result.size());

        CountFilterByCategoryRes first = result.get(0);
        assertEquals(1L, first.categoryId());
        assertEquals("카페", first.categoryName());
        assertEquals(15, first.sumCountFilterByCategory());

        CountFilterByCategoryRes second = result.get(1);
        assertEquals(2L, second.categoryId());
        assertEquals("패션", second.categoryName());
        assertEquals(8, second.sumCountFilterByCategory());

        verify(actionLogsRepository).findCountFilterByActionType(ActionType.FILTER_USED);
    }

    @DisplayName("카테고리, 브랜드별 추천 통계 조회 - 성공")
    @Test
    void findCountRecommendationByCategoryAndBrand_success() {
        // given
        RecommendationsByBrand brand1 = RecommendationsByBrand.of("스타벅스", 12);
        RecommendationsByBrand brand2 = RecommendationsByBrand.of("이디야", 8);
        CountRecommendationRes category1 = CountRecommendationRes.of(1L, "카페", 20, List.of(brand1, brand2));

        RecommendationsByBrand brand3 = RecommendationsByBrand.of("무신사", 6);
        CountRecommendationRes category2 = CountRecommendationRes.of(2L, "패션", 6, List.of(brand3));

        List<CountRecommendationRes> mockResult = List.of(category1, category2);

        when(recommendationRepository.findCountRecommendationByCategory()).thenReturn(mockResult);

        // when
        List<CountRecommendationRes> result = adminService.findCountRecommendationByCategoryAndBrand();

        // then
        assertEquals(2, result.size());

        CountRecommendationRes res1 = result.get(0);
        assertEquals(1L, res1.categoryId());
        assertEquals("카페", res1.categoryName());
        assertEquals(20, res1.sumRecommendationByCategory());
        assertEquals(2, res1.recommendationsByBrandList().size());
        assertEquals("스타벅스", res1.recommendationsByBrandList().get(0).brandName());
        assertEquals(12, res1.recommendationsByBrandList().get(0).sumRecommendationsByBrand());

        CountRecommendationRes res2 = result.get(1);
        assertEquals(2L, res2.categoryId());
        assertEquals("패션", res2.categoryName());
        assertEquals(6, res2.sumRecommendationByCategory());
        assertEquals(1, res2.recommendationsByBrandList().size());
        assertEquals("무신사", res2.recommendationsByBrandList().get(0).brandName());
        assertEquals(6, res2.recommendationsByBrandList().get(0).sumRecommendationsByBrand());

        verify(recommendationRepository).findCountRecommendationByCategory();
    }

    @DisplayName("카테고리, 브랜드별 추천 통계 조회 - 빈 리스트")
    @Test
    void findCountRecommendationByCategoryAndBrand_emptyDataset() {
        // given
        when(recommendationRepository.findCountRecommendationByCategory()).thenReturn(Collections.emptyList());

        // when
        List<CountRecommendationRes> result = adminService.findCountRecommendationByCategoryAndBrand();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty(), "빈 리스트가 반환되어야 합니다.");

        verify(recommendationRepository).findCountRecommendationByCategory();
    }

    @DisplayName("카테고리, 브랜드별 멤버십 사용횟수 통계 조회 - 성공")
    @Test
    void findCountMembershipUsageByCategoryAndBrand_success() {
        // given
        List<MembershipUsageByBrand> usageList1 = List.of(
                MembershipUsageByBrand.of("브랜드A", 5),
                MembershipUsageByBrand.of("브랜드B", 3)
        );

        List<MembershipUsageByBrand> usageList2 = List.of(
                MembershipUsageByBrand.of("브랜드C", 2)
        );

        List<CountMembershipUsageRes> expected = List.of(
                CountMembershipUsageRes.of(1L, "카테고리1", 8, usageList1),
                CountMembershipUsageRes.of(2L, "카테고리2", 2, usageList2)
        );

        when(historyRepository.findCountMembershipUsageByCategory()).thenReturn(expected);

        // when
        List<CountMembershipUsageRes> result = adminService.findCountMembershipUsageByCategoryAndBrand();

        // then
        assertEquals(2, result.size());
        assertEquals("카테고리1", result.get(0).categoryName());
        assertEquals(8, result.get(0).sumMembershipUsageByCategory());
        assertEquals("브랜드A", result.get(0).membershipUsageByBrandList().get(0).brandName());
        verify(historyRepository).findCountMembershipUsageByCategory();
    }

    @DisplayName("카테고리, 브랜드별 멤버십 사용횟수 통계 조회 - 빈 리스트")
    @Test
    void findCountMembershipUsageByCategoryAndBrand_emptyDataset() {
        // given
        when(historyRepository.findCountMembershipUsageByCategory()).thenReturn(List.of());

        // when
        List<CountMembershipUsageRes> result = adminService.findCountMembershipUsageByCategoryAndBrand();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(historyRepository).findCountMembershipUsageByCategory();
    }
}