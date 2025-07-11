package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationBaseDataRepository;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.dto.response.UpdateUserRes;
import com.ureca.uhyu.domain.user.entity.Marker;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.repository.MarkerRepository;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MarkerRepository markerRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private RecommendationBaseDataRepository recommendationRepository;

    @InjectMocks
    private UserService userService;

    private User createUser() {
        Marker marker = Marker.builder().markerImage("marker.jpg").build();
        setId(marker, 1L);

        User user = User.builder()
                .userName("홍길동")
                .kakaoId(456465L)
                .email("asdad@kakao.com")
                .age((byte) 32)
                .gender(Gender.MALE)
                .role(UserRole.TMP_USER)
                .status(Status.ACTIVE)
                .grade(Grade.GOOD)
                .profileImage("asdsad.png")
                .nickname("nick")
                .marker(marker)
                .build();
        setId(user, 1L);
        return user;
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
        // given
        User user = createUser();
        setId(user, 1L);
        LocalDateTime timeValue = LocalDateTime.now();
        setUpdatedAt(user, timeValue);

        // when
        GetUserInfoRes getUserInfoRes = userService.findUserInfo(user);

        // then
        assertEquals("홍길동", getUserInfoRes.userName());
        assertEquals("asdsad.png", getUserInfoRes.profileImage());
        assertEquals("nick", getUserInfoRes.nickName());
        assertEquals("asdad@kakao.com", getUserInfoRes.email());
        assertEquals((byte) 32, getUserInfoRes.age());
        assertEquals(Gender.MALE, getUserInfoRes.gender());
        assertEquals(timeValue, getUserInfoRes.updatedAt());
        assertEquals(Grade.GOOD, getUserInfoRes.grade());
    }

    @DisplayName("개인정보 수정 - 성공")
    @Test
    void updateUserInfoSuccess() {
        // given
        User user = createUser();
        setId(user, 1L);

        Marker marker2 = Marker.builder().markerImage("marker2.jpg").build();
        setId(marker2, 2L);

        UpdateUserReq request = new UpdateUserReq(
                "asdsad2.png",
                "nick2",
                Grade.VIP,
                List.of(1L, 2L, 3L),
                2L
        );

        // mock
        Mockito.when(markerRepository.findById(2L)).thenReturn(Optional.of(marker2));

        for (Long brandId : request.updatedBrandIdList()) {
            Brand brand = Brand.builder().brandName("브랜드" + brandId).build();
            setId(brand, brandId);
            Mockito.when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        }

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UpdateUserRes updateUserRes = userService.updateUserInfo(user, request);

        // then
        assertEquals(1L, updateUserRes.userId());
        assertEquals("asdsad2.png", user.getProfileImage());
        assertEquals("nick2", user.getNickname());
        assertEquals(Grade.VIP, user.getGrade());
        assertEquals(marker2, user.getMarker());

        Mockito.verify(recommendationRepository).deleteByUserAndDataType(user, DataType.INTEREST);
        Mockito.verify(recommendationRepository, Mockito.times(3)).save(Mockito.any());
    }

    @DisplayName("개인정보 수정 - 존재하지 않는 마커 ID로 실패")
    @Test
    void updateUserInfoFail_InvalidMarker() {
        // given
        User user = createUser();
        setId(user, 1L);
        Long invalidMarkerId = 999L;

        UpdateUserReq request = new UpdateUserReq(
                null,
                null,
                null,
                null,
                invalidMarkerId
        );

        // mock
        Mockito.when(markerRepository.findById(invalidMarkerId)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userService.updateUserInfo(user, request);
        });

        assertEquals(ResultCode.INVALID_INPUT, exception.getResultCode());
    }

    @DisplayName("개인정보 수정 - 존재하지 않는 브랜드 ID로 실패")
    @Test
    void updateUserInfoFail_InvalidBrand() {
        // given
        User user = createUser();
        setId(user, 1L);
        Long invalidBrandId = 888L;

        UpdateUserReq request = new UpdateUserReq(
                null,
                null,
                null,
                List.of(invalidBrandId),
                null
        );

        // mock
        Mockito.when(brandRepository.findById(invalidBrandId)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userService.updateUserInfo(user, request);
        });

        assertEquals(ResultCode.INVALID_INPUT, exception.getResultCode());
    }
}
