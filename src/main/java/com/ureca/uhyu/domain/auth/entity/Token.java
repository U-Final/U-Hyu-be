package com.ureca.uhyu.domain.auth.entity;

import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Token extends BaseEntity {

    @Column(name = "refresh_token", nullable = false, length = 500)
    private String refreshToken;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expireDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public void updateRefreshToken(String refreshToken, LocalDateTime expireDate) {
        this.refreshToken = refreshToken;
        this.expireDate = expireDate;
    }
}
