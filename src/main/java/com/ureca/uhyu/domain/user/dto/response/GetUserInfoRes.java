package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 정보 조회 응답")
public record GetUserInfoRes(
        String userName,
        String email,
        int age,
        Gender gender,
        Grade grade
) {
    public static GetUserInfoRes from(User user){
        return new GetUserInfoRes(
                user.getUserName(),
                user.getEmail(),
                user.getAge(),
                user.getGender(),
                user.getGrade()
        );
    }
}
