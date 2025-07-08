package com.ureca.uhyu.domain.brand.entity;

import com.ureca.uhyu.global.entity.BaseEntity;
import com.ureca.uhyu.global.enums.Grade;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Benefit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Benefit extends BaseEntity {

    private String description;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
}