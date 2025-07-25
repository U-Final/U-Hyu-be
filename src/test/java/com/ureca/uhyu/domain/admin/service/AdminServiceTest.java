package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByBrandRes;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.CountFilterByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.UserBrandPair;
import com.ureca.uhyu.domain.user.enums.ActionType;
import com.ureca.uhyu.domain.user.repository.actionLogs.ActionLogsRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private ActionLogsRepository actionLogsRepository;

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
        List<BookmarksByCategoryRes> result = adminService.findBookmarksByCategoryAndBrand();

        // then
        assertEquals(1, result.size());
        BookmarksByCategoryRes categoryRes = result.get(0);
        assertEquals(10L, categoryRes.categoryId());
        assertEquals("카테고리A", categoryRes.categoryName());
        assertEquals(3, categoryRes.sumBookmarksByCategory()); // 2 + 1

        List<BookmarksByBrandRes> brandList = categoryRes.bookmarksByBrandList();
        assertEquals(2, brandList.size());

        Map<String, Integer> brandCountMap = brandList.stream()
                .collect(Collectors.toMap(BookmarksByBrandRes::brandName, BookmarksByBrandRes::sumBookmarksByBrand));
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
        List<BookmarksByCategoryRes> result = adminService.findBookmarksByCategoryAndBrand();

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
        assertEquals(15, first.sumBookmarksByCategory());

        CountFilterByCategoryRes second = result.get(1);
        assertEquals(2L, second.categoryId());
        assertEquals("패션", second.categoryName());
        assertEquals(8, second.sumBookmarksByCategory());

        verify(actionLogsRepository).findCountFilterByActionType(ActionType.FILTER_USED);
    }
}