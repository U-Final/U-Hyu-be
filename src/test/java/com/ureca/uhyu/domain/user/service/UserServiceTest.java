package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.domain.user.enums.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User createUser() {
        return User.builder()
                .userName("홍길동")
                .kakaoId(456465L)
                .email("asdad@kakao.com")
                .age((byte)32)
                .gender(Gender.MALE)
                .role(UserRole.TMP_USER)
                .status(Status.ACTIVE)
                .grade(Grade.GOOD)
                .profileImage("asdsad.png")
                .nickname("nick")
                .build();
    }

    private void setId(Object target, Long idValue) {
        try {
            Field idField = target.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(target, idValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpdatedAt(Object target, LocalDateTime timeValue) {
        try {
            Field idField = target.getClass().getSuperclass().getDeclaredField("updatedAt");
            idField.setAccessible(true);
            idField.set(target, timeValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("개인정보 조회")
    @Test
    void findUserInfo() {
        //given
        User user = createUser();
        setId(user, 1L);
        LocalDateTime timeValue = LocalDateTime.now();
        setUpdatedAt(user, timeValue);

        //when
        GetUserInfoRes getUserInfoRes =  userService.findUserInfo(user);

        //then
        assertEquals("홍길동", getUserInfoRes.userName());
        assertEquals("asdsad.png", getUserInfoRes.profileImage());
        assertEquals("nick", getUserInfoRes.nickName());
        assertEquals("asdad@kakao.com", getUserInfoRes.email());
        assertEquals((byte) 32, getUserInfoRes.age());
        assertEquals(Gender.MALE, getUserInfoRes.gender());
        assertEquals(timeValue, getUserInfoRes.updatedAt());
        assertEquals(Grade.GOOD, getUserInfoRes.grade());
    }
}