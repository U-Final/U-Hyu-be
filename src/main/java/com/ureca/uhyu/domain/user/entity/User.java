package com.ureca.uhyu.domain.user.entity;

import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(length = 20, nullable = false)
    private String userName;

    @Column(length = 20)
    private String nickname;

    @Column(unique = true)
    private Long kakaoId;

    @Column(length = 200, unique = true)
    private String email;

    private Byte age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(length = 10)
    private Grade grade;

    @Column(length = 500)
    private String profileImage;

    public void withdraw() {
        this.status = Status.DELETED;
        this.updatedAt = LocalDateTime.now(); // 업데이트 시간도 갱신
    }

    public UserRole getUserRole() {
        return role;
    }
}