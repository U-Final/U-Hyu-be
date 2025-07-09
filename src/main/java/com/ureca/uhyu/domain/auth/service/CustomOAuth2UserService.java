package com.ureca.uhyu.domain.auth.service;

import com.ureca.uhyu.domain.auth.dto.CustomOAuth2User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * 카카오로부터 사용자 정보를 받아오는 부분
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        // kakao_id(pk)
        Long kakaoId = Long.valueOf(attributes.get("id").toString());

        // profile_nickname
        String nickname = profile.get("nickname") != null ? profile.get("nickname").toString() : "사용자";

        // profile_image
        String profileImage = profile.get("profile_image_url") != null ? profile.get("profile_image_url").toString() : null;

        // account_email
        String email = kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString()
                : "temp_kakao_" + kakaoId + "@example.com";

        // name
        String name = kakaoAccount.get("name") != null ? kakaoAccount.get("name").toString() : "익명";

        // gender
        String genderStr = kakaoAccount.get("gender") != null ? kakaoAccount.get("gender").toString() : null;
        Gender gender = null;
        if ("male".equalsIgnoreCase(genderStr)) gender = Gender.MALE;
        else if ("female".equalsIgnoreCase(genderStr)) gender = Gender.FEMALE;


        // 신규/기존 유저에 따라 redirect 위치 달라짐
        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);
        User user;
        boolean isNewUser;

        // 신규 / 기존 유저 구분
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            isNewUser = false;
        } else {
            user = userRepository.save(User.builder()
                    .kakaoId(kakaoId)
                    .userName(nickname)
                    .email(email)
                    .profileImage(profileImage)
                    .gender(gender)
                    .status(Status.ACTIVE)
                    .role(UserRole.USER)
                    .build());
            isNewUser = true;
        }

        // 토큰 생성에 필요한 정보들
        return new CustomOAuth2User(nickname, user.getId(), user.getRole(), isNewUser);
    }

    public void expireCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
    }
}
