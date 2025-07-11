package com.ureca.uhyu.domain.brand.entity;

import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.global.entity.BaseEntity;
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
}