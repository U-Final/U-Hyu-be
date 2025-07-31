package com.ureca.uhyu.domain.recommendation.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.guest.dto.response.GuestRecommendationRes;
import com.ureca.uhyu.domain.guest.service.GuestService;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private GuestService guestService;

    @DisplayName("비로그인 추천 - 인기 브랜드 1~3개 반환")
    @Test
    void getTop3PopularBrandsForGuest_success() {
        // given
        Brand brand1 = createBrand("CGV", "cgv.png");
        setId(brand1, 1L);
        Brand brand2 = createBrand("롯데시네마", "lotte.png");
        setId(brand2, 2L);

        List<Brand> brands = List.of(brand1, brand2);
        when(recommendationRepository.findTop3BrandByVisitCountFromHistory()).thenReturn(brands);

        // when
        List<GuestRecommendationRes> result = guestService.getTop3PopularBrandsForGuest();

        // then
        assertEquals(2, result.size());
        assertEquals("CGV", result.get(0).brandName());
        assertEquals("롯데시네마", result.get(1).brandName());
    }

    @DisplayName("비로그인 추천 - 인기 브랜드 없음")
    @Test
    void getTop3PopularBrandsForGuest_empty() {
        // given
        when(recommendationRepository.findTop3BrandByVisitCountFromHistory()).thenReturn(Collections.emptyList());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            guestService.getTop3PopularBrandsForGuest();
        });

        assertEquals(ResultCode.NOT_FOUND_RECOMMENDATION_FOR_USER, exception.getResultCode());
    }

    private Brand createBrand(String name, String logoImage) {
        return Brand.builder()
                .brandName(name)
                .logoImage(logoImage)
                .build();
    }

    private void setId(Object target, Long idValue) {
        try {
            Field idField = target.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(target, idValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}