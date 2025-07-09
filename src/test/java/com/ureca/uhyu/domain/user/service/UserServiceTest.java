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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        assertEquals(timeValue, getUserInfoRes.updatedAt());
        assertEquals(Grade.GOOD, getUserInfoRes.grade());
    }

    @DisplayName("개인정보 수정 - 성공")
    @Test
    void updateUserInfoSuccess() {
        //given
        Marker marker1 = Marker.builder().markerImage("marker1.jpg").build();
        setId(marker1, 1L);
        Marker marker2 = Marker.builder().markerImage("marker2.jpg").build();
        setId(marker2, 2L);

        User user = User.builder()
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
                .marker(marker1)
                .build();
        setId(user, 100L);

        UpdateUserReq request = new UpdateUserReq(
                "asdsad2.png",
                "nick2",
                List.of(1L, 2L, 3L),
                2L
        );

        // Marker, Brand mock 설정
        Mockito.when(markerRepository.findById(2L)).thenReturn(Optional.of(marker2));
        for (Long brandId : request.updatedBrandIdList()) {
            Brand brand = Brand.builder().brandName("브랜드" + brandId).build();
            setId(brand, brandId);
            Mockito.when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        }

        // 저장된 user 반환 mock
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        UpdateUserRes updateUserRes = userService.updateUserInfo(user, request);

        //then
        //코드 수정 필요(mock으로 수정된 개체 만들어서 repo에서 가져와야함)
        assertEquals("asdsad2.png", updateUserRes.profileImage());
        assertEquals("nick2", updateUserRes.nickname());
        assertEquals(2L, updateUserRes.markerId());
        // 관심 브랜드 개수 확인 (추가된 Recommendation 수는 3개여야 함)
        Mockito.verify(recommendationRepository, Mockito.times(3)).save(Mockito.any());
        Mockito.verify(recommendationRepository).deleteByUserAndDataType(user, DataType.INTEREST);
    }

    @DisplayName("개인정보 수정 - 예외 발생")
    @Test
    void updateUserInfoException() {
        //given
        User user = createUser();
        setId(user, 1L);

        //when

        //then

    }
}