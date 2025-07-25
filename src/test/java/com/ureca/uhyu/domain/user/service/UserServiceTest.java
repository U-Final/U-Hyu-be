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
import com.ureca.uhyu.domain.user.dto.response.UserStatisticsRes;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.repository.*;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.repository.actionLogs.ActionLogsRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkListRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import com.ureca.uhyu.domain.user.repository.history.HistoryRepository;
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
    private BrandRepository brandRepository;

    @Mock
    private RecommendationBaseDataRepository recommendationRepository;

    @Mock
    private BookmarkListRepository bookmarkListRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private ActionLogsRepository actionLogsRepository;

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
        assertEquals(32, getUserInfoRes.age());
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

        Long markerId2 = 2L;

        UpdateUserReq request = new UpdateUserReq(
                "asdsad2.png",
                "nick2",
                Grade.VIP,
                List.of(1L, 2L, 3L),
                markerId2
        );

        // mock
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
        assertEquals(markerId2, user.getMarkerId());

        Mockito.verify(recommendationRepository).deleteByUserAndDataType(user, DataType.INTEREST);
        Mockito.verify(recommendationRepository, Mockito.times(3)).save(Mockito.any());
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

        BookmarkList bookmarkList = createBookmarkList(user);
        setId(bookmarkList, 10L);

        Brand brand = createBrand("브랜드A", "logo.png");
        setId(brand, 20L);

        Store store = createStore(brand, "스토어A", "서울시 마포구");
        setId(store, 30L);

        Bookmark bookmark = createBookmark(bookmarkList, store);
        setId(bookmark, 100L);

        List<Bookmark> bookmarks = List.of(bookmark);

        // mock
        when(bookmarkListRepository.findByUser(user)).thenReturn(Optional.of(bookmarkList));
        when(bookmarkRepository.findByBookmarkList(bookmarkList)).thenReturn(bookmarks);

        // when
        List<BookmarkRes> result = userService.findBookmarkList(user);

        // then
        assertEquals(1, result.size());

        BookmarkRes res = result.get(0);
        assertEquals(100L, res.bookmarkId());
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

        BookmarkList bookmarkList = createBookmarkList(user);
        setId(bookmarkList, 10L);

        Brand brand = createBrand("브랜드A", "logo.png");
        setId(brand, 20L);

        Store store = createStore(brand, "매장 A", "경기도 고양시");
        setId(store, 30L);

        Bookmark bookmark = createBookmark(bookmarkList, store);
        setId(bookmark, 100L);

        // mock
        when(bookmarkRepository.findById(100L)).thenReturn(Optional.of(bookmark));

        // when
        userService.deleteBookmark(user, 100L);

        // then
        verify(bookmarkRepository).delete(bookmark);
    }

    @DisplayName("즐겨찾기 삭제 - 유저 인증 실패")
    @Test
    void deleteBookmarkFail_User() {
        // given
        User owner = createUser();
        setId(owner, 1L);

        User attacker = createUser();
        setId(attacker, 2L); // 다른 유저

        BookmarkList bookmarkList = createBookmarkList(owner);
        setId(bookmarkList, 10L);

        Brand brand = createBrand("브랜드A", "logo.png");
        setId(brand, 20L);

        Store store = createStore(brand, "매장A", "경기도 고양시");
        setId(store, 30L);

        Bookmark bookmark = createBookmark(bookmarkList, store);
        setId(bookmark, 100L);

        // mock
        when(bookmarkRepository.findById(100L)).thenReturn(Optional.of(bookmark));

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userService.deleteBookmark(attacker, 100L);
        });

        assertEquals(ResultCode.FORBIDDEN, exception.getResultCode());
        verify(bookmarkRepository, never()).delete(any());
    }

    @DisplayName("즐겨찾기 삭제 - 잘못된 북마크 접근으로 인한 실패")
    @Test
    void deleteBookmarkFail_NotFound() {
        // given
        User user = createUser();
        setId(user, 1L);

        // mock
        when(bookmarkRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userService.deleteBookmark(user, 999L);
        });

        assertEquals(ResultCode.BOOKMARK_NOT_FOUND, exception.getResultCode());
        verify(bookmarkRepository, never()).delete(any());
    }

    @DisplayName("사용자 활동 내역 조회")
    @Test
    void findUserStatistics() {
        // given
        User user = createUser();
        setId(user, 1L);

        Integer discountMoney = 12345;

        // 가장 많이 조회한 브랜드
        Brand brand1 = createBrand("브랜드A", "logo1.png");
        Brand brand2 = createBrand("브랜드B", "logo2.png");
        Brand brand3 = createBrand("브랜드C", "logo3.png");
        Brand brand4 = createBrand("브랜드D", "logo4.png");
        setId(brand1, 1L);
        setId(brand2, 2L);
        setId(brand3, 3L);
        setId(brand4, 4L);

        List<Brand> topBrands = List.of(brand2, brand4, brand1);

        // 가장 최근 방문 매장
        Store store1 = createStore(brand1, "스토어1", "서울시 강남구");
        Store store2 = createStore(brand2, "스토어2", "서울시 마포구");
        Store store3 = createStore(brand3, "스토어3", "서울시 종로구");
        setId(store1, 11L);
        setId(store2, 12L);
        setId(store3, 13L);

        List<Store> recentStores = List.of(store1, store2, store3);

        // mock
        when(historyRepository.findDiscountMoneyThisMonth(user)).thenReturn(discountMoney);
        when(actionLogsRepository.findTop3ClickedBrands(user)).thenReturn(topBrands);
        when(historyRepository.findRecentStoreInMonth(user)).thenReturn(recentStores);

        // when
        UserStatisticsRes result = userService.findUserStatistics(user);

        // then
        assertEquals(discountMoney, result.discountMoney());

        // 가장 많이 조회한 브랜드
        assertEquals(3, result.bestBrandList().size());

        assertEquals(2, result.bestBrandList().get(0).bestBrandId());
        assertEquals("브랜드B", result.bestBrandList().get(0).bestBrandName());
        assertEquals("logo2.png", result.bestBrandList().get(0).bestBrandImage());

        assertEquals(4, result.bestBrandList().get(1).bestBrandId());
        assertEquals("브랜드D", result.bestBrandList().get(1).bestBrandName());
        assertEquals("logo4.png", result.bestBrandList().get(1).bestBrandImage());

        assertEquals(1, result.bestBrandList().get(2).bestBrandId());
        assertEquals("브랜드A", result.bestBrandList().get(2).bestBrandName());
        assertEquals("logo1.png", result.bestBrandList().get(2).bestBrandImage());

        // 가장 최근 방문 매장
        assertEquals(3, result.recentStoreList().size());

        assertEquals(11L, result.recentStoreList().get(0).recentStoreId());
        assertEquals("스토어1", result.recentStoreList().get(0).recentStoreName());
        assertEquals("logo1.png", result.recentStoreList().get(0).recentBrandImage());

        assertEquals(12L, result.recentStoreList().get(1).recentStoreId());
        assertEquals("스토어2", result.recentStoreList().get(1).recentStoreName());
        assertEquals("logo2.png", result.recentStoreList().get(1).recentBrandImage());

        assertEquals(13L, result.recentStoreList().get(2).recentStoreId());
        assertEquals("스토어3", result.recentStoreList().get(2).recentStoreName());
        assertEquals("logo3.png", result.recentStoreList().get(2).recentBrandImage());
    }

    private User createUser() {
        Long markerId = 1L;

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
                .markerId(markerId)
                .build();

        return user;
    }

    private BookmarkList createBookmarkList(User user) {
        return BookmarkList.builder()
                .user(user)
                .build();
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

    private Store createStore(Brand brand, String name, String address) {
        Store store = Store.builder()
                .name(name)
                .addrDetail(address)
                .geom(null)  // 필요시 값 넣어두기
                .brand(brand)
                .build();
        return store;
    }

    private Bookmark createBookmark(BookmarkList bookmarkList, Store store) {
        Bookmark bookmark = Bookmark.builder()
                .bookmarkList(bookmarkList)
                .store(store)
                .build();
        return bookmark;
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
