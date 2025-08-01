package com.ureca.uhyu.domain.brand.entity;

import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.global.entity.BaseEntity;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String brandName;

    private String logoImage;

    private String usageMethod;

    private String usageLimit;

    @Enumerated(EnumType.STRING)
    private StoreType storeType;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Store> stores;

    @Setter
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Benefit> benefits;

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void updateBrandInfo(String brandName, String logoImage, String usageMethod, String usageLimit, StoreType storeType) {
        this.brandName = brandName;
        this.logoImage = logoImage;
        this.usageMethod = usageMethod;
        this.usageLimit = usageLimit;
        this.storeType = storeType;
    }

    public String getBenefitDescriptionByGradeOrDefault(Grade grade) {
        return this.benefits.stream()
                .filter(b -> b.getGrade() == grade)
                .findFirst()
                .or(() -> this.benefits.stream()
                        .filter(b -> b.getGrade() == Grade.GOOD)
                        .findFirst())
                .map(Benefit::getDescription)
                .orElseThrow(() -> new GlobalException(ResultCode.GRADE_GOOD_NOT_FOUND));
    }
}