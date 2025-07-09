package com.ureca.uhyu.domain.auth.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.ureca.uhyu.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * CustomOAuth2UserService 에서 사용되는 클래스 SNS 로그인을 통해 Authentication 객체를 생성하기위한 클래스
 */
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final String username;
    private final Long userId;
    private final UserRole userRole;
    private final boolean isNewUser; // 신규/기존 회원 확인

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // spring security 권한 부여 : 현재 인증된 사용자의 역할 부여
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userRole.name())); //
    }

    // 인증된 사용자 정보들
    @Override
    public String getName() { return username; }

    public Long getUserId() { return userId; }

    public UserRole getUserRole() { return userRole; }

    public boolean isNewUser() { return isNewUser;}
}