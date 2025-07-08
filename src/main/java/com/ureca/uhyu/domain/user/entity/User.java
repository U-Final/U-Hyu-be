package com.ureca.uhyu.domain.user.entity;

import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Role;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.global.entity.BaseEntity;
import com.ureca.uhyu.global.enums.Grade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "kakao_id", nullable = false)
    private Long kakaoId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "grade", nullable = false)
    private Grade grade;

    @Column(name = "profile_image", nullable = false)
    private String profileImage;
}
