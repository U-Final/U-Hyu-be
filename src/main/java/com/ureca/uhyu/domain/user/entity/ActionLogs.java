package com.ureca.uhyu.domain.user.entity;

import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "action_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ActionLogs extends BaseEntity {

    // TODO : erd에 관해 질문 사항이 있어 회의 후 구현(행동 타입 왜 이넘 아닌지 / 카테고리 id는 어디갔는지)
    @Column(length = 20)
    String actionType;

    @Column(name = "store_id", nullable = true)
    Long storeId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
