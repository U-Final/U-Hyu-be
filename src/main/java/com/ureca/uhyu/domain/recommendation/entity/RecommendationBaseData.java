package com.ureca.uhyu.domain.recommendation.entity;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recommendation_base_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecommendationBaseData extends BaseEntity {

    @Column(nullable = false)
    DataType dataType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    Brand brand;
}
