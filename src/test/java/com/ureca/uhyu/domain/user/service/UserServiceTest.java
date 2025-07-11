package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationBaseDataRepository;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.response.BookmarkRes;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.dto.response.UpdateUserRes;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarksList;
import com.ureca.uhyu.domain.user.entity.Marker;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.repository.BookmarkRepository;
import com.ureca.uhyu.domain.user.repository.BookmarksListRepository;
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
import static org.mockito.Mockito.*;

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

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BookmarksListRepository bookmarksListRepository;

    @InjectMocks
    private UserService userService;

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
        when(markerRepository.findById(2L)).thenReturn(Optional.of(marker2));

        for (Long brandId : request.updatedBrandIdList()) {
            Brand brand = Brand.builder().brandName("브랜드" + brandId).build();
            setId(brand, brandId);
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        }

        when(userRepository.save(Mockito.any(User.class)))
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
        when(markerRepository.findById(invalidMarkerId)).thenReturn(Optional.empty());

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
        when(brandRepository.findById(invalidBrandId)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userService.updateUserInfo(user, request);
        });

        assertEquals(ResultCode.INVALID_INPUT, exception.getResultCode());
    }

    @DisplayName("즐겨찾기 목록 조회 - 성공")
    @Test
    void findBookmarkList() {
        // given
        User user = createUser();
        setId(user, 1L);

        Bookmark bookmark = createBookmark(user);
        setId(bookmark, 10L);

        Brand brand = createBrand();
        setId(brand, 20L);

        Store store = createStore(brand);
        setId(store, 30L);

        BookmarksList bookmarksList = createBookmarksList(bookmark, store);
        setId(bookmarksList, 100L);

        List<BookmarksList> bookmarks = List.of(bookmarksList);

        // mock
        when(bookmarkRepository.findByUser(user)).thenReturn(bookmark);
        when(bookmarksListRepository.findByBookmark(bookmark)).thenReturn(bookmarks);

        // when
        List<BookmarkRes> result = userService.findBookmarkList(user);

        // then
        assertEquals(1, result.size());

        BookmarkRes res = result.get(0);
        assertEquals(100L, res.bookmarksListId());
        assertEquals(30L, res.storeId());
        assertEquals("logo.png", res.logoImage());
        assertEquals("스토어A", res.storeName());
        assertEquals("서울시 마포구", res.addressDetail());
        assertNull(res.benefit());  // TODO 로직 결정 후 benefit 부분 테스트 코드 수정 예정
    }

    @DisplayName("즐겨찾기 삭제 - 성공")
    @Test
    void deleteBookmarkSuccess() {
        // given
        User user = createUser();
        setId(user, 1L);

        Bookmark bookmark = createBookmark(user);
        setId(bookmark, 10L);

        Brand brand = createBrand();
        setId(brand, 20L);

        Store store = createStore(brand);
        setId(store, 30L);

        BookmarksList bookmarksList = createBookmarksList(bookmark, store);
        setId(bookmarksList, 100L);

        // mock
        when(bookmarksListRepository.findById(100L)).thenReturn(Optional.of(bookmarksList));

        // when
        userService.deleteBookmark(user, 100L);

        // then
        verify(bookmarksListRepository).delete(bookmarksList);
    }

    @DisplayName("즐겨찾기 삭제 - 유저 인증 실패")
    @Test
    void deleteBookmarkFail_User() {
        // given
        User owner = createUser();
        setId(owner, 1L);

        User attacker = createUser();
        setId(attacker, 2L); // 다른 유저

        Bookmark bookmark = createBookmark(owner);
        setId(bookmark, 10L);

        Brand brand = createBrand();
        setId(brand, 20L);

        Store store = createStore(brand);
        setId(store, 30L);

        BookmarksList bookmarksList = createBookmarksList(bookmark, store);
        setId(bookmarksList, 100L);

        // mock
        when(bookmarksListRepository.findById(100L)).thenReturn(Optional.of(bookmarksList));

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userService.deleteBookmark(attacker, 100L);
        });

        assertEquals(ResultCode.FORBIDDEN, exception.getResultCode());
        verify(bookmarksListRepository, never()).delete(any());
    }

    @DisplayName("즐겨찾기 삭제 - 잘못된 북마크 접근으로 인한 실패")
    @Test
    void deleteBookmarkFail_NotFound() {
        // given
        User user = createUser();
        setId(user, 1L);

        // mock
        when(bookmarksListRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userService.deleteBookmark(user, 999L);
        });

        assertEquals(ResultCode.BOOKMARK_NOT_FOUND, exception.getResultCode());
        verify(bookmarksListRepository, never()).delete(any());
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

    private Bookmark createBookmark(User user) {
        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .build();
        return bookmark;
    }

    private Brand createBrand() {
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
                .brandName("브랜드A")
                .logoImage("logo.png")
                .usageMethod("모바일 바코드 제시")
                .usageLimit("1일 1회")
                .storeType(StoreType.OFFLINE)
                .benefits(List.of(benefit))
                .build();
        return brand;
    }

    private Store createStore(Brand brand) {
        Store store = Store.builder()
                .name("스토어A")
                .addrDetail("서울시 마포구")
                .geom(null)  // 필요시 값 넣어두기
                .brand(brand)
                .build();
        return store;
    }

    private BookmarksList createBookmarksList(Bookmark bookmark, Store store) {
        BookmarksList bookmarksList = BookmarksList.builder()
                .bookmark(bookmark)
                .store(store)
                .build();
        return bookmarksList;
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
}
