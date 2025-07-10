package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.enums.UserRole;

public record UserInfoResponse (
    String name,
    String nickname,
    String email,
    Byte age,
    Gender gender,
    UserRole userRole,
    Status status,
    Grade grade,
    String profile_image
){
}
