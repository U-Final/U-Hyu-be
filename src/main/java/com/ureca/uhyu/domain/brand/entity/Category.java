package com.ureca.uhyu.domain.brand.entity;

import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
        import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Brand> brands;
}
