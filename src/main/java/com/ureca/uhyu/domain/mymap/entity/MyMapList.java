package com.ureca.uhyu.domain.mymap.entity;

import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "my_map_list")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MyMapList extends BaseEntity {

    @Column(length = 30, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarkerColor markerColor;

    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    @OneToMany(mappedBy = "myMapList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyMap> myMaps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateMyMapList(
            String title, MarkerColor markerColor
    ){
        this.title = title;
        this.markerColor = markerColor;
    }
}
