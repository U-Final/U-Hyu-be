package com.ureca.uhyu.domain.recommendation.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastApiRecommendationClient {

    private final WebClient webClient;

    public void requestRecomputeRecommendation(Long userId) {
        try {
            webClient.post()
                    .uri("/re-recommendation") // FastAPI 엔드포인트에 맞게 수정
                    .bodyValue(Map.of("user_id", userId))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block(); // 동기 호출

            log.info("FastAPI 재추천 요청 성공 - userId: {}", userId);
        } catch (Exception e) {
            log.error("FastAPI 재추천 요청 실패 - userId: {}, message: {}", userId, e.getMessage());
        }
    }
}

