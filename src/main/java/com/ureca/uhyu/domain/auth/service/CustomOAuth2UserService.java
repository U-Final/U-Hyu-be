package com.ureca.uhyu.domain.auth.service;

import com.ureca.uhyu.domain.auth.dto.CustomOAuth2User;
import com.ureca.uhyu.domain.auth.dto.KakaoUserInfoResponse;
import com.ureca.uhyu.domain.auth.mapper.UserInfoMapper;
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

        KakaoUserInfoResponse userInfo = extractUserInfo(attributes);

        Optional<User> optionalUser = userRepository.findByKakaoId(userInfo.kakaoId());
        User user;
        boolean isNewUser;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            isNewUser = false;
        } else {
            user = createNewUser(userInfo);
            isNewUser = true;
        }

        return new CustomOAuth2User(userInfo.nickname(), user.getId(), user.getRole(), isNewUser);
    }

    private KakaoUserInfoResponse extractUserInfo(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        Long kakaoId = Long.valueOf(attributes.get("id").toString());
        String nickname = profile.getOrDefault("nickname", "사용자").toString();
        String profileImage = profile.getOrDefault("profile_image_url", null) != null
                ? profile.get("profile_image_url").toString() : null;
        String email = kakaoAccount.getOrDefault("email", "temp_kakao_" + kakaoId + "@example.com").toString();
        String name = kakaoAccount.getOrDefault("name", "익명").toString();
        String genderStr = kakaoAccount.get("gender") != null ? kakaoAccount.get("gender").toString() : null;
        Gender gender = parseGender(genderStr);
        String age_range = kakaoAccount.get("age_range") != null ? kakaoAccount.get("age_range").toString() : null;
        String birthyear = kakaoAccount.get("birthyear") != null ? kakaoAccount.get("birthyear").toString() : null;

        return new KakaoUserInfoResponse(kakaoId, nickname, email, name, profileImage, gender, age_range, birthyear);
    }

    private Gender parseGender(String genderStr) {
        if ("male".equalsIgnoreCase(genderStr)) return Gender.MALE;
        if ("female".equalsIgnoreCase(genderStr)) return Gender.FEMALE;
        return null;
    }

    private User createNewUser(KakaoUserInfoResponse userInfo) {
        return userRepository.save(UserInfoMapper.toUserEntity(userInfo));
    }
}

