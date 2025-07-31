package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "유저 정보 조회 응답")
public record GetUserInfoRes(
        String profileImage,
        String userName,
        String nickName,
        String email,
        Integer age,
        Gender gender,
        Grade grade,
        UserRole role,
        LocalDateTime updatedAt,
        List<Long> interestedBrandList
) {
    public static GetUserInfoRes from(User user, List<Brand> brand) {
        return new GetUserInfoRes(
                user.getProfileImage(),
                user.getUserName(),
                user.getNickname(),
                user.getEmail(),
                user.getAge(),
                user.getGender(),
                user.getGrade(),
                user.getUserRole(),
                user.getUpdatedAt(),
                brand.stream().map(Brand::getId).toList()
        );
    }
}
