package com.ureca.uhyu.domain.user.entity;

import com.ureca.uhyu.domain.user.enums.ActionType;
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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ActionType actionType;

    @Column(name = "store_id", nullable = true)
    private Long storeId;

    @Column(name = "category_id", nullable = true)
    Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
