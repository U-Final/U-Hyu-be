package com.ureca.uhyu.domain.store.entity;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "Store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Store extends BaseEntity {

    private String name;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point geom;

    private String addrDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
}