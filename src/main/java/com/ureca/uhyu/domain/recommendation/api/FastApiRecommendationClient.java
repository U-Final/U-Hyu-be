package com.ureca.uhyu.domain.recommendation.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastApiRecommendationClient {

    private final WebClient webClient;

    public void requestRecomputeRecommendation(Long userId) {
        try {
            webClient.post()
                    .uri("/re-recommendation")
                    .bodyValue(Map.of("user_id", userId))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(10))
                    .retry(3)
                    .subscribe(
                            unused -> log.info("FastAPI 재추천 요청 성공 - userId: {}", userId),
                            error -> log.error("FastAPI 재추천 요청 실패 - userId: {}, message: {}", userId, error.getMessage())
                    );
        } catch (Exception e) {
            log.error("FastAPI 재추천 요청 설정 자체 실패 - userId: {}, message: {}", userId, e.getMessage());
        }
    }
}

