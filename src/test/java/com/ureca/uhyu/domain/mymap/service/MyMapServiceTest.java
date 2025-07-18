package com.ureca.uhyu.domain.mymap.service;

import com.ureca.uhyu.domain.mymap.dto.response.MyMapRes;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import com.ureca.uhyu.domain.mymap.repository.MyMapListRepository;
import com.ureca.uhyu.domain.user.entity.Marker;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
        List<MyMapRes> result = myMapService.findMyMapList(user);

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
        List<MyMapRes> result = myMapService.findMyMapList(user);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
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