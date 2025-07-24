package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "유저 정보 조회 응답")
public record GetUserInfoRes(
        String profileImage,
        String userName,
        String nickName,
        String email,
        Integer age,
        Gender gender,
        Grade grade,
        LocalDateTime updatedAt
) {
    public static GetUserInfoRes from(User user){
        return new GetUserInfoRes(
                user.getProfileImage(),
                user.getUserName(),
                user.getNickname(),
                user.getEmail(),
                user.getAge(),
                user.getGender(),
                user.getGrade(),
                user.getUpdatedAt()
        );
    }
}
