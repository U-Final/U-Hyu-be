package com.ureca.uhyu.domain.recommendation.entity;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자가 온보딩에서 입력하거나 마이페이지에서 수정한 관심 브랜드
 */

@Entity
@Table(name = "recommendation_base_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecommendationBaseData extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataType dataType;
}
