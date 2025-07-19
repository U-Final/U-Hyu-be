package com.ureca.uhyu.domain.mymap.service;

import com.ureca.uhyu.domain.mymap.dto.request.CreateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.request.UpdateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.response.MyMapListRes;
import com.ureca.uhyu.domain.mymap.dto.response.UpdateMyMapListRes;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import com.ureca.uhyu.domain.mymap.repository.MyMapListRepository;
import com.ureca.uhyu.domain.user.entity.Marker;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyMapServiceTest {

    @Mock
    private MyMapListRepository myMapListRepository;

    @InjectMocks
    private MyMapService myMapService;

    @DisplayName("mymap 목록 조회 - 성공")
    @Test
    void findMyMapList() {
        // given
        User user = createUser();
        setId(user, 1L);

        MyMapList map1 = createMyMapList(user, "map1", MarkerColor.GREEN);
        setId(map1, 1L);
        MyMapList map2 = createMyMapList(user, "map2", MarkerColor.RED);
        setId(map2, 2L);

        List<MyMapList> myMapLists = List.of(map1, map2);

        // mock
        when(myMapListRepository.findByUser(user)).thenReturn(myMapLists);

        // when
        List<MyMapListRes> result = myMapService.findMyMapList(user);

        // then
        assertEquals(2, result.size());

        assertEquals(map1.getId(), result.get(0).myMapListId());
        assertEquals(map1.getTitle(), result.get(0).title());
        assertEquals(map1.getMarkerColor(), result.get(0).markerColor());

        assertEquals(map2.getId(), result.get(1).myMapListId());
        assertEquals(map2.getTitle(), result.get(1).title());
        assertEquals(map2.getMarkerColor(), result.get(1).markerColor());
    }

    @DisplayName("mymap 목록 조회 - 빈 리스트 반환")
    @Test
    void findMyMapList_EmptyList() {
        // given
        User user = createUser();
        setId(user, 1L);

        // mock
        when(myMapListRepository.findByUser(user)).thenReturn(List.of());

        // when
        List<MyMapListRes> result = myMapService.findMyMapList(user);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @DisplayName("mymap 생성 - 성공")
    @Test
    void testCreateMyMapList_success() {
        // given
        User user = createUser();
        setId(user, 1L);

        CreateMyMapListReq request = new CreateMyMapListReq("나만의 지도", MarkerColor.GREEN, "uuid-1234");

        MyMapList unsaved = MyMapList.builder()
                .title(request.title())
                .markerColor(request.markerColor())
                .uuid(request.uuid())
                .user(user)
                .build();

        MyMapList saved = MyMapList.builder()
                .title(request.title())
                .markerColor(request.markerColor())
                .uuid(request.uuid())
                .user(user)
                .build();
        setId(saved, 100L);

        when(myMapListRepository.save(any(MyMapList.class))).thenReturn(saved);

        // when
        Long result = myMapService.createMyMapList(user, request);

        // then
        assertEquals(100L, result);
        verify(myMapListRepository, times(1)).save(any(MyMapList.class));
    }

    @DisplayName("mymap 수정 - 성공")
    @Test
    void updateMyMapList_success() {
        // given
        User user = createUser();
        setId(user, 1L);

        MyMapList myMapList = createMyMapList(user, "기존 제목", MarkerColor.GREEN);
        setId(myMapList, 100L);

        UpdateMyMapListReq req = new UpdateMyMapListReq(
                100L,
                "변경된 제목",
                MarkerColor.YELLOW
        );

        when(myMapListRepository.findById(100L)).thenReturn(Optional.of(myMapList));
        when(myMapListRepository.save(any(MyMapList.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        UpdateMyMapListRes result = myMapService.updateMyMapList(user, req);

        // then
        assertEquals(100L, result.myMapListId());

        verify(myMapListRepository).findById(100L);
        verify(myMapListRepository).save(myMapList);
    }

    @DisplayName("MyMap 수정 - 다른 사용자의 마이맵이면 FORBIDDEN 예외 발생")
    @Test
    void updateMyMapList_forbidden() {
        // given
        User owner = createUser(); // 진짜 소유자
        setId(owner, 1L);

        User attacker = createUser(); // 공격자
        setId(attacker, 2L);

        MyMapList myMapList = createMyMapList(owner, "원래 제목", MarkerColor.RED);
        setId(myMapList, 100L);

        UpdateMyMapListReq req = new UpdateMyMapListReq(
                100L,
                "변경 시도",
                MarkerColor.YELLOW
        );

        when(myMapListRepository.findById(100L)).thenReturn(Optional.of(myMapList));

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () ->
                myMapService.updateMyMapList(attacker, req)
        );

        assertEquals(ResultCode.FORBIDDEN, exception.getResultCode());
    }

    @DisplayName("My Map List 삭제 - 성공")
    @Test
    void deleteMyMapList_success() {
        // given
        User user = createUser();
        setId(user, 1L);

        MyMapList myMapList = createMyMapList(user, "기존 제목", MarkerColor.GREEN);
        setId(myMapList, 100L);

        // mock
        when(myMapListRepository.findById(100L)).thenReturn(Optional.of(myMapList));

        // when
        myMapService.deleteMyMapList(user, 100L);

        // then
        verify(myMapListRepository).delete(myMapList);
    }

    @DisplayName("My Map List 삭제 - 유저 인증 실패")
    @Test
    void deleteBookmarkFail_User() {
        // given
        User user = createUser();
        setId(user, 1L);

        User attacker = createUser();
        setId(attacker, 2L); // 다른 유저

        MyMapList myMapList = createMyMapList(user, "기존 제목", MarkerColor.GREEN);
        setId(myMapList, 100L);

        // mock
        when(myMapListRepository.findById(100L)).thenReturn(Optional.of(myMapList));

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            myMapService.deleteMyMapList(attacker, 100L);
        });

        assertEquals(ResultCode.FORBIDDEN, exception.getResultCode());
        verify(myMapListRepository, never()).delete(any());
    }

    @DisplayName("My Map List 삭제 - 잘못된 My Map List 접근으로 인한 실패")
    @Test
    void deleteBookmarkFail_NotFound() {
        // given
        User user = createUser();
        setId(user, 1L);

        // mock
        when(myMapListRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            myMapService.deleteMyMapList(user, 999L);
        });

        assertEquals(ResultCode.MY_MAP_LIST_NOT_FOUND, exception.getResultCode());
        verify(myMapListRepository, never()).delete(any());
    }

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

        return user;
    }

    private MyMapList createMyMapList(User user, String mapName, MarkerColor markerColor) {
        return MyMapList.builder()
                .user(user)
                .title(mapName)
                .markerColor(markerColor)
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
}
