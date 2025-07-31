package com.ureca.uhyu.domain.user.entity;

import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Grade grade;

    @Column(length = 500)
    private String profileImage;

    @Column(length = 20)
    private String age_range;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Barcode barcode;

    public void withdraw() {
        this.status = Status.DELETED;
        this.updatedAt = LocalDateTime.now(); // 업데이트 시간도 갱신
    }

    public UserRole getUserRole() {
        return role;
    }

    public void updateUser(
            String profileImage, String nickname, Grade grade
    ){
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.grade = grade;
    }

    public void updateUserInfo(Integer age, Gender gender, String ageRange, Grade grade, UserRole role) {
        this.age = age;
        this.gender = gender;
        this.age_range = ageRange;
        this.grade = grade;
        this.role = role;
        this.updatedAt = LocalDateTime.now(); // update timestamp
    }
}
