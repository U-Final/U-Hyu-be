package com.ureca.uhyu.domain.recommendation.event;

import com.ureca.uhyu.domain.recommendation.api.FastApiRecommendationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Slf4j
@Component
public class RecommendationEventListener {

    private final FastApiRecommendationClient fastApiRecommendationClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecommendationEvent(RecommendationEvent event){
        log.info("트랜잭션 커밋 후 추천 재계산 요청: userId={}",event.userId());
        fastApiRecommendationClient.requestRecomputeRecommendation(event.userId());
    }
}
