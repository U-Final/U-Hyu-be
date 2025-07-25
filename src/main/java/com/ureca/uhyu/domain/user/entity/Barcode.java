package com.ureca.uhyu.domain.user.entity;

import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="barcode")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Barcode extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "image_url")
    private String imageURL;

    public void updateImageUrl(String imageUrl){
        this.imageURL = imageUrl;
    }
}
