package com.ureca.uhyu.domain.recommendation.controller;

import com.ureca.uhyu.domain.recommendation.dto.response.RecommendationRes;
import com.ureca.uhyu.domain.recommendation.service.RecommendationService;
import com.ureca.uhyu.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    private static final String RECOMMEND_URL = "/recommendation";

    private MockMvc mockMvc;

    @InjectMocks
    private RecommendationController recommendationController;

    @Mock
    private RecommendationService recommendationService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(recommendationController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("userId", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("가장 최신 추천 결과 Top3 브랜드를 반환한다")
    void it_returns_top3_recommendations() throws Exception {
        // given
        List<RecommendationRes> mockResponse = List.of(
                new RecommendationRes(1L, "스타벅스", 1),
                new RecommendationRes(2L, "이디야", 2),
                new RecommendationRes(3L, "투썸플레이스", 3)
        );

        given(recommendationService.getLatestTop3Recommendations(any(User.class)))
                .willReturn(mockResponse);

        // when
        ResultActions result = mockMvc.perform(get(RECOMMEND_URL)
                        .header("Authorization", "Bearer mock-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].brandName").value("스타벅스"))
                .andExpect(jsonPath("$.data[0].rank").value(1))
                .andExpect(jsonPath("$.data[1].brandName").value("이디야"))
                .andExpect(jsonPath("$.data[2].brandName").value("투썸플레이스"));
    }
}