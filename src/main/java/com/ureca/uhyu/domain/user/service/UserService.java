package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.dto.response.UpdateUserRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public GetUserInfoRes findUserInfo(User user) {
        return GetUserInfoRes.from(user);
    }

    public UpdateUserRes updateByUser(User user) {
        return new UpdateUserRes(user.getId()); //임시용
    }
}
