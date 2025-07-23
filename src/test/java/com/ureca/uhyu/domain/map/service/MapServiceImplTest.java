package com.ureca.uhyu.domain.map.service;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.map.dto.response.MapBookmarkRes;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepository;
import com.ureca.uhyu.domain.store.repository.StoreRepositoryCustom;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.repository.BookmarkListRepository;
import com.ureca.uhyu.domain.user.repository.BookmarkRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapServiceImplTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreRepositoryCustom storeRepositoryCustom;

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

            assertThat(res.benefits().grade()).isEqualTo("GOOD");
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
