package com.ureca.uhyu.domain.auth.mapper;

import com.ureca.uhyu.domain.auth.dto.KakaoUserInfoResponse;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.enums.UserRole;

public class UserInfoMapper {

    public static User toUserEntity(KakaoUserInfoResponse dto) {
        Integer age = calculateAge(dto.birthyear());
        String ageRange = dto.age_range();

        if (ageRange == null && age != null) {
            int lowerBound = (age / 10) * 10;
            int upperBound = lowerBound + 9;
            ageRange = lowerBound + "~" + upperBound;
        }

        return User.builder()
                .kakaoId(dto.kakaoId())
                .userName(dto.nickname())
                .email(dto.email())
                .profileImage(dto.profileImage())
                .gender(dto.gender())
                .age(age)
                .age_range(ageRange)
                .status(Status.ACTIVE)
                .role(UserRole.TMP_USER)
                .build();
    }

    private static Integer calculateAge(String birthyear) {
        if (birthyear == null) return null;
        try {
            int birth = Integer.parseInt(birthyear);
            int now = java.time.Year.now().getValue();
            return now - birth;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}