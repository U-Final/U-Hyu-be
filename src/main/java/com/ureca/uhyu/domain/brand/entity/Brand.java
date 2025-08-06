package com.ureca.uhyu.domain.brand.entity;

import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.global.entity.BaseEntity;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @Builder.Default
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Benefit> benefits = new ArrayList<>();

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void updateBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void updateBrandImg(String logoImage) {
        this.logoImage = logoImage;
    }

    public void updateUsageMethod(String usageMethod) {
        this.usageMethod = usageMethod;
    }

    public void updateUsageLimit(String usageLimit) {
        this.usageLimit = usageLimit;
    }

    public void updateStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    public void setBenefits(List<Benefit> benefits) {
        this.benefits.clear();
        this.benefits.addAll(benefits);
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