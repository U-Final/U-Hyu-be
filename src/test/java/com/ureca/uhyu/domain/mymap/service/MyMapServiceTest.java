package com.ureca.uhyu.domain.mymap.service;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.mymap.dto.request.CreateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.request.UpdateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.response.*;
import com.ureca.uhyu.domain.mymap.entity.MyMap;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import com.ureca.uhyu.domain.mymap.repository.MyMapListRepository;
import com.ureca.uhyu.domain.mymap.repository.MyMapRepository;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepository;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.Marker;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.BookmarkListRepository;
import com.ureca.uhyu.domain.user.repository.BookmarkRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
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

    private GeometryFactory geometryFactory = new GeometryFactory();

    @Mock
    private MyMapListRepository myMapListRepository;

    @Mock
    private MyMapRepository myMapRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BookmarkListRepository bookmarkListRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private MyMapService myMapService;

    @DisplayName("mymap 목록 조회 - 성공")
    @Test
    void findMyMapList() {
        // given
        User user = createUser();
        setId(user, 1L);

        MyMapList map1 = createMyMapList(user, "map1", MarkerColor.GREEN, "uuid1");
        setId(map1, 1L);
        MyMapList map2 = createMyMapList(user, "map2", MarkerColor.RED, "uuid2");
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
        assertEquals(map1.getUuid(), result.get(0).uuid());

        assertEquals(map2.getId(), result.get(1).myMapListId());
        assertEquals(map2.getTitle(), result.get(1).title());
        assertEquals(map2.getMarkerColor(), result.get(1).markerColor());
        assertEquals(map2.getUuid(), result.get(1).uuid());
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
        CreateMyMapListRes result = myMapService.createMyMapList(user, request);

        // then
        assertEquals(100L, result.myMapListId());
        verify(myMapListRepository, times(1)).save(any(MyMapList.class));
    }

    @DisplayName("mymap 수정 - 성공")
    @Test
    void updateMyMapList_success() {
        // given
        User user = createUser();
        setId(user, 1L);

        MyMapList myMapList = createMyMapList(user, "기존 제목", MarkerColor.GREEN, "uuid1");
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

        MyMapList myMapList = createMyMapList(owner, "원래 제목", MarkerColor.RED, "uuid1");
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

        MyMapList myMapList = createMyMapList(user, "기존 제목", MarkerColor.GREEN, "uuid1");
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
    void deleteMyMapList_User() {
        // given
        User user = createUser();
        setId(user, 1L);

        User attacker = createUser();
        setId(attacker, 2L); // 다른 유저

        MyMapList myMapList = createMyMapList(user, "기존 제목", MarkerColor.GREEN, "uuid1");
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
    void deleteMyMapList_NotFound() {
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

    @DisplayName("uuid 기반 My Map 지도 조회 - 성공")
    @Test
    void findMyMap() {
        // given
        User user = createUser();
        setId(user, 1L);

        String uuid = "test-uuid";

        Brand brand = createBrand("test_brand", "brand.img");
        setId(brand, 50L);

        Store store1 = createStore("test_store1", "test_addr", brand);
        setId(store1, 70L);
        Store store2 = createStore("test_store2", "test_addr2", brand);
        setId(store2, 90L);

        MyMapList myMapList = createMyMapList(user, "MyMap 1", MarkerColor.GREEN, "uuid1");
        setId(myMapList, 100L);

        MyMap myMap1 = createMyMap(myMapList, store1);
        setId(myMap1, 400L);
        MyMap myMap2 = createMyMap(myMapList, store2);
        setId(myMap2, 401L);

        List<MyMap> myMaps = List.of(myMap1, myMap2);

        // mock
        when(myMapListRepository.findByUuid(uuid)).thenReturn(Optional.of(myMapList));
        when(myMapRepository.findByMyMapList(myMapList)).thenReturn(myMaps);

        // when
        MyMapRes result = myMapService.findMyMap(user, uuid);

        // then
        assertNotNull(result);
        assertEquals(myMapList.getMarkerColor(), result.markerColor());
        assertEquals(myMapList.getTitle(), result.title());
        assertEquals(myMapList.getId(), result.myMapListId());
        assertEquals(myMapList.getUuid(), result.uuid());
        assertTrue(result.isMine());
        verify(myMapListRepository).findByUuid(uuid);
        verify(myMapRepository).findByMyMapList(myMapList);
    }

    @DisplayName("My Map 매장 등록 유무 조회 - 성공")
    @Test
    void findMyMapListWithIsBookmarked_success() {
        // given
        User user = createUser();
        setId(user, 1L);

        Brand brand = createBrand("테스트 브랜드", "logo.png");
        setId(brand, 10L);

        Store store = createStore("테스트 매장", "서울시 강남구", brand);
        setId(store, 100L);
        Store store2 = createStore("다른 매장", "주소", brand);
        setId(store2, 101L);

        MyMapList list1 = createMyMapList(user, "마이맵 1", MarkerColor.GREEN, "uuid-1");
        MyMapList list2 = createMyMapList(user, "마이맵 2", MarkerColor.YELLOW, "uuid-2");
        setId(list1, 200L);
        setId(list2, 201L);
        List<MyMapList> lists = List.of(list1, list2);

        MyMap map1 = createMyMap(list1, store); // list1에 포함
        MyMap map2 = createMyMap(list2, store2); // list2에는 포함 안 됨
        List<MyMap> myMaps = List.of(map1, map2);

        BookmarkList bookmarkList = BookmarkList.builder().user(user).build();
        setId(bookmarkList, 300L);

        Bookmark bookmark = Bookmark.builder().bookmarkList(bookmarkList).store(store).build();
        setId(bookmark, 301L);

        // mock
        when(storeRepository.findById(100L)).thenReturn(Optional.of(store));
        when(myMapListRepository.findByUser(user)).thenReturn(List.of(list1, list2));
        when(myMapRepository.findByMyMapListIn(lists)).thenReturn(List.of(map1, map2));
        when(bookmarkListRepository.findByUser(user)).thenReturn(Optional.of(bookmarkList));
        when(bookmarkRepository.findByBookmarkList(bookmarkList)).thenReturn(List.of(bookmark));

        // when
        BookmarkedMyMapRes result = myMapService.findMyMapListWithIsBookmarked(user, 100L);

        // then
        assertNotNull(result);
        assertEquals("테스트 매장", result.storeName());
        assertTrue(result.isBookmarked());
        assertEquals(2, result.bookmarkedMyMapLists().size());

        BookmarkedMyMapListRes res1 = result.bookmarkedMyMapLists().get(0);
        assertEquals(200L, res1.myMapListId());
        assertEquals("마이맵 1", res1.title());
        assertEquals(MarkerColor.GREEN, res1.markerColor());
        assertTrue(res1.isMyMapped());

        BookmarkedMyMapListRes res2 = result.bookmarkedMyMapLists().get(1);
        assertEquals(201L, res2.myMapListId());
        assertEquals("마이맵 2", res2.title());
        assertEquals(MarkerColor.YELLOW, res2.markerColor());
        assertFalse(res2.isMyMapped());

        verify(storeRepository).findById(100L);
        verify(myMapListRepository).findByUser(user);
        verify(myMapRepository, times(1)).findByMyMapListIn(any());
        verify(bookmarkListRepository).findByUser(user);
        verify(bookmarkRepository).findByBookmarkList(bookmarkList);
    }

    @DisplayName("My Map 매장 등록 유무 조회 - 스토어 ID가 존재하지 않아 실패")
    @Test
    void findMyMapListWithIsBookmarked_fail_storeNotFound() {
        // given
        User user = createUser();
        setId(user, 1L);
        Long invalidStoreId = 999L;

        when(storeRepository.findById(invalidStoreId)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            myMapService.findMyMapListWithIsBookmarked(user, invalidStoreId);
        });

        assertEquals(ResultCode.NOT_FOUND_STORE, exception.getResultCode());
        verify(storeRepository).findById(invalidStoreId);
    }

    @DisplayName("My Map 매장 등록 유무 조회 - 사용자의 북마크 리스트가 존재하지 않아 실패")
    @Test
    void findMyMapListWithIsBookmarked_fail_bookmarkListNotFound() {
        // given
        User user = createUser();
        setId(user, 1L);

        Brand brand = createBrand("브랜드", "logo.png");
        setId(brand, 10L);
        Store store = createStore("매장", "서울시", brand);
        setId(store, 100L);

        when(storeRepository.findById(100L)).thenReturn(Optional.of(store));
        when(myMapListRepository.findByUser(user)).thenReturn(List.of());
        when(bookmarkListRepository.findByUser(user)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            myMapService.findMyMapListWithIsBookmarked(user, 100L);
        });

        assertEquals(ResultCode.BOOKMARK_LIST_NOT_FOUND, exception.getResultCode());
        verify(bookmarkListRepository).findByUser(user);
    }

    @DisplayName("MyMap 토글 - 매장 등록 성공")
    @Test
    void toggleMyMap_addSuccess() {
        // given
        User user = createUser();
        setId(user, 1L);

        Brand brand = createBrand("브랜드A", "logo.png");
        Store store = createStore("스토어A", "서울시 강남구", brand);
        setId(store, 10L);

        MyMapList myMapList = createMyMapList(user, "Map1", MarkerColor.RED, "uuid1");
        setId(myMapList, 100L);

        // mock
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(myMapListRepository.findById(100L)).thenReturn(Optional.of(myMapList));
        when(myMapRepository.findByMyMapListAndStore(myMapList, store)).thenReturn(Optional.empty());

        // when
        ToggleMyMapRes result = myMapService.toggleMyMap(user, 10L, 100L);

        // then
        assertTrue(result.isMyMapped());
        assertEquals(store.getId(), result.storeId());
        assertEquals(myMapList.getId(), result.myMapListId());

        verify(myMapRepository).save(any(MyMap.class));
        verify(myMapRepository, never()).delete(any(MyMap.class));
    }

    @DisplayName("MyMap 토글 - 매장 해제 성공")
    @Test
    void toggleMyMap_removeSuccess() {
        // given
        User user = createUser();
        setId(user, 1L);

        Brand brand = createBrand("브랜드A", "logo.png");
        Store store = createStore("스토어A", "서울시 강남구", brand);
        setId(store, 10L);

        MyMapList myMapList = createMyMapList(user, "Map1", MarkerColor.RED, "uuid1");
        setId(myMapList, 100L);

        MyMap existing = createMyMap(myMapList, store);
        setId(existing, 200L);

        // mock
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(myMapListRepository.findById(100L)).thenReturn(Optional.of(myMapList));
        when(myMapRepository.findByMyMapListAndStore(myMapList, store)).thenReturn(Optional.of(existing));

        // when
        ToggleMyMapRes result = myMapService.toggleMyMap(user, 10L, 100L);

        // then
        assertFalse(result.isMyMapped());
        verify(myMapRepository).delete(existing);
        verify(myMapRepository, never()).save(any(MyMap.class));
    }

    @DisplayName("MyMap 토글 - 다른 유저의 마이맵에 접근 시 실패")
    @Test
    void toggleMyMap_forbidden() {
        // given
        User owner = createUser(); // 실제 마이맵 주인
        setId(owner, 1L);
        User attacker = createUser(); // 요청하는 사용자
        setId(attacker, 2L);

        Brand brand = createBrand("브랜드A", "logo.png");
        Store store = createStore("스토어A", "서울시 강남구", brand);
        setId(store, 10L);

        MyMapList myMapList = createMyMapList(owner, "Map1", MarkerColor.GREEN, "uuid1");
        setId(myMapList, 100L);

        // mock
        when(storeRepository.findById(10L)).thenReturn(Optional.of(store));
        when(myMapListRepository.findById(100L)).thenReturn(Optional.of(myMapList));

        // when & then
        GlobalException ex = assertThrows(GlobalException.class, () -> {
            myMapService.toggleMyMap(attacker, 10L, 100L);
        });

        assertEquals(ResultCode.FORBIDDEN, ex.getResultCode());
    }

    private User createUser() {
        Marker marker = Marker.builder().markerImage("marker.jpg").build();
        setId(marker, 1L);

        User user = User.builder()
                .userName("홍길동")
                .kakaoId(456465L)
                .email("asdad@kakao.com")
                .age((Integer) 32)
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

    private Brand createBrand(String name, String image) {
        Category category = Category.builder()
                .categoryName("카테고리A")
                .build();
        setId(category, 1L);

        Benefit benefit = Benefit.builder()
                .description("혜택1")
                .build();
        setId(benefit, 2L);

        Brand brand = Brand.builder()
                .category(category)
                .brandName(name)
                .logoImage(image)
                .usageMethod("모바일 바코드 제시")
                .usageLimit("1일 1회")
                .storeType(StoreType.OFFLINE)
                .benefits(List.of(benefit))
                .build();
        return brand;
    }

    private Point createPoint(double latitude, double longitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude)); // 위도(Y), 경도(X)
    }

    private Store createStore(String name, String addrDetail, Brand brand) {
        return Store.builder()
                .name(name)
                .addrDetail(addrDetail)
                .geom(createPoint(123.23, 37.2312))
                .brand(brand)
                .build();
    }

    private MyMapList createMyMapList(User user, String mapName, MarkerColor markerColor, String uuid) {
        return MyMapList.builder()
                .user(user)
                .title(mapName)
                .markerColor(markerColor)
                .uuid(uuid)
                .build();
    }

    private MyMap createMyMap(MyMapList myMapList, Store store) {
        return MyMap.builder()
                .myMapList(myMapList)
                .store(store)
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
