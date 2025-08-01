package com.ureca.uhyu.domain.map.service;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.map.dto.response.MapBookmarkRes;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.recommendation.entity.Recommendation;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepository;
import com.ureca.uhyu.domain.store.repository.StoreRepositoryCustom;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Gender;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.Status;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkListRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MapServiceImplTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreRepositoryCustom storeRepositoryCustom;

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private MapServiceImpl mapService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BookmarkListRepository bookmarkListRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final Point dummyPoint = geometryFactory.createPoint(new Coordinate(127.0, 37.5));

    private final Category category = Category.builder()
            .categoryName("카페")
            .build();

    private final Brand brand = Brand.builder()
            .brandName("이디야")
            .category(category)
            .logoImage("logo.jpg")
            .usageLimit("1일 1회")
            .usageMethod("매장 제시")
            .build();

    private final Store store = Store.builder()
            .id(1L)
            .name("이디야 판교점")
            .addrDetail("경기 성남시")
            .brand(brand)
            .geom(dummyPoint)
            .build();

    private User createUser() {
        Long markerId = 1L;

        User user = User.builder()
                .userName("홍길동")
                .kakaoId(456465L)
                .email("asdad@kakao.com")
                .age(32)
                .gender(Gender.MALE)
                .role(UserRole.TMP_USER)
                .status(Status.ACTIVE)
                .grade(Grade.GOOD)
                .profileImage("asdsad.png")
                .nickname("nick")
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

    private Recommendation createRecommendation(User user, Brand brand) {
        return Recommendation.builder()
                .userId(user.getId())
                .brand(brand)
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

    @DisplayName("근처 추천 매장 목록 조회 - 성공")
    @Test
    void findRecommendedStores_success() {
        // given
        User user = createUser();
        setId(user, 1L);
        double lat = 37.5665;
        double lon = 126.9780;

        Brand brand1 = createBrand("브랜드1", "logo1.png");
        Brand brand2 = createBrand("브랜드2", "logo2.png");
        setId(brand1, 10L);
        setId(brand2, 11L);

        Recommendation rec1 = createRecommendation(user, brand1);
        Recommendation rec2 = createRecommendation(user, brand2);
        List<Recommendation> recommendations = List.of(rec1, rec2);

        Store store1 = createStore("스토어A", "주소1", brand1);
        Store store2 = createStore("스토어B", "주소2", brand2);
        setId(store1, 100L);
        setId(store2, 101L);
        List<Store> stores = List.of(store1, store2);

        // mock
        when(recommendationRepository.findTop3ByUserOrderByCreatedAtDescRankAsc(user.getId()))
                .thenReturn(recommendations);
        when(storeRepositoryCustom.findNearestStores(lat, lon, List.of(10L, 11L)))
                .thenReturn(stores);

        // when
        List<MapRes> result = mapService.findRecommendedStores(lat, lon, user);

        // then
        assertEquals(2, result.size());
        assertEquals("스토어A", result.get(0).storeName());
        assertEquals("스토어B", result.get(1).storeName());

        verify(recommendationRepository).findTop3ByUserOrderByCreatedAtDescRankAsc(user.getId());
        verify(storeRepositoryCustom).findNearestStores(lat, lon, List.of(10L, 11L));
    }

    @DisplayName("근처 추천 매장 목록 조회 - 브랜드가 null일 경우 예외 발생")
    @Test
    void findRecommendedStores_brandIsNull_throwsException() {
        // given
        User user = createUser();
        setId(user, 1L);
        double lat = 37.0;
        double lon = 127.0;

        Recommendation invalidRec = Recommendation.builder()
                .userId(user.getId())
                .brand(null) // 브랜드가 null인 추천
                .build();

        List<Recommendation> invalidRecommendations = List.of(invalidRec);

        when(recommendationRepository.findTop3ByUserOrderByCreatedAtDescRankAsc(user.getId()))
                .thenReturn(invalidRecommendations);

        // when & then
        GlobalException ex = assertThrows(GlobalException.class, () ->
                mapService.findRecommendedStores(lat, lon, user)
        );

        assertEquals(ResultCode.BRAND_ID_IS_NULL, ex.getResultCode());
        verify(recommendationRepository).findTop3ByUserOrderByCreatedAtDescRankAsc(user.getId());
        verifyNoInteractions(storeRepositoryCustom);
    }

    @Nested
    class GetFilteredStoresTest {

        @Test
        void shouldReturnStoresWithCategoryAndBrandFilters() {
            double lat = 37.5;
            double lon = 127.0;
            double radius = 1000.0;
            String categoryName = "카페";
            String brandName = "이디야";

            when(storeRepositoryCustom.findStoresByFilters(lat, lon, radius, categoryName, brandName))
                    .thenReturn(List.of(store));

            List<MapRes> result = mapService.getFilteredStores(lat, lon, radius, categoryName, brandName);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).storeName()).isEqualTo("이디야 판교점");
        }

        @Test
        void shouldReturnStoresWithOnlyCategoryFilter() {
            double lat = 37.5;
            double lon = 127.0;
            double radius = 1000.0;
            String categoryName = "카페";

            when(storeRepositoryCustom.findStoresByFilters(lat, lon, radius, categoryName, null))
                    .thenReturn(List.of(store));

            List<MapRes> result = mapService.getFilteredStores(lat, lon, radius, categoryName, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).storeName()).isEqualTo("이디야 판교점");
        }

        @Test
        void shouldReturnStoresWithOnlyBrandFilter() {
            double lat = 37.5;
            double lon = 127.0;
            double radius = 1000.0;
            String brandName = "이디야";

            when(storeRepositoryCustom.findStoresByFilters(lat, lon, radius, null, brandName))
                    .thenReturn(List.of(store));

            List<MapRes> result = mapService.getFilteredStores(lat, lon, radius, null, brandName);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).storeName()).isEqualTo("이디야 판교점");
        }

        @Test
        void shouldReturnStoresWithoutAnyFilter() {
            double lat = 37.5;
            double lon = 127.0;
            double radius = 1000.0;

            when(storeRepositoryCustom.findStoresByFilters(lat, lon, radius, null, null))
                    .thenReturn(List.of(store));

            List<MapRes> result = mapService.getFilteredStores(lat, lon, radius, null, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).storeName()).isEqualTo("이디야 판교점");
        }
    }

    @Nested
    class GetStoreDetailTest {

        @Test
        void shouldReturnBenefitMatchingUserGrade() {
            User user = mock(User.class);
            when(user.getGrade()).thenReturn(Grade.VIP);

            Benefit vipBenefit = Benefit.builder()
                    .grade(Grade.VIP)
                    .description("음료 2잔 무료")
                    .brand(brand)
                    .build();

            brand.setBenefits(List.of(vipBenefit));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

            StoreDetailRes res = mapService.getStoreDetail(1L, user);

            assertThat(res.benefits().benefitText()).isEqualTo("음료 2잔 무료");
            assertThat(res.usageLimit()).isEqualTo("1일 1회");
        }

        @Test
        void shouldReturnGoodBenefitIfNoExactMatch() {
            User user = mock(User.class);
            when(user.getGrade()).thenReturn(Grade.VIP);

            Benefit goodBenefit = Benefit.builder()
                    .grade(Grade.GOOD)
                    .description("1+1 혜택")
                    .brand(brand)
                    .build();

            brand.setBenefits(List.of(goodBenefit));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

            StoreDetailRes res = mapService.getStoreDetail(1L, user);

            assertThat(res.benefits().grade()).isEqualTo("VIP");
            assertThat(res.benefits().benefitText()).isEqualTo("1+1 혜택");
        }

        @Test
        void shouldReturnNullIfNoBenefitExists() {
            User user = mock(User.class);
            when(user.getGrade()).thenReturn(Grade.VIP);

            brand.setBenefits(List.of());
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

            StoreDetailRes res = mapService.getStoreDetail(1L, user);

            assertThat(res.benefits()).isNull();
        }

        @Test
        void shouldIncludeFavoriteInfoInStoreDetail() {
            User user = mock(User.class);
            when(user.getGrade()).thenReturn(Grade.VIP);

            Benefit vipBenefit = Benefit.builder()
                    .grade(Grade.VIP)
                    .description("VIP 혜택")
                    .brand(brand)
                    .build();

            brand.setBenefits(List.of(vipBenefit));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(bookmarkRepository.existsByBookmarkListUserAndStore(user, store)).thenReturn(true);
            when(bookmarkRepository.countByStore(store)).thenReturn(42);

            StoreDetailRes res = mapService.getStoreDetail(1L, user);

            assertThat(res.isFavorite()).isTrue();
            assertThat(res.favoriteCount()).isEqualTo(42);
        }
    }

    @Nested
    class ToggleBookmarkTest {

        @Test
        void shouldToggleBookmark_addIfNotExists() {
            User user = mock(User.class);
            BookmarkList bookmarkList = BookmarkList.builder().user(user).build();

            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(bookmarkListRepository.findByUser(user)).thenReturn(Optional.of(bookmarkList));
            when(bookmarkRepository.findByBookmarkListAndStore(bookmarkList, store)).thenReturn(Optional.empty());

            MapBookmarkRes res = mapService.toggleBookmark(user, 1L);

            assertThat(res.storeId()).isEqualTo(1L);
            assertThat(res.isBookmarked()).isTrue(); // 추가된 상태
        }

        @Test
        void shouldToggleBookmark_removeIfExists() {
            User user = mock(User.class);
            BookmarkList bookmarkList = BookmarkList.builder().user(user).build();
            Bookmark bookmark = Bookmark.builder().bookmarkList(bookmarkList).store(store).build();

            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(bookmarkListRepository.findByUser(user)).thenReturn(Optional.of(bookmarkList));
            when(bookmarkRepository.findByBookmarkListAndStore(bookmarkList, store)).thenReturn(Optional.of(bookmark));

            MapBookmarkRes res = mapService.toggleBookmark(user, 1L);

            assertThat(res.storeId()).isEqualTo(1L);
            assertThat(res.isBookmarked()).isFalse(); // 삭제된 상태
        }
    }
}
