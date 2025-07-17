package com.ureca.uhyu.domain.mymap.entity;

import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import com.ureca.uhyu.domain.mymap.enums.Visibility;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "my_map_list")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MyMapList extends BaseEntity {

    @Column(length = 30)
    String title;

    @Column(name = "description", nullable = false)
    String description;

    @Enumerated(EnumType.STRING)
    MarkerColor markerColor;

    @Enumerated(EnumType.STRING)
    Visibility visibility;

    @Column(name = "uuid", nullable = false)
    Long uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
