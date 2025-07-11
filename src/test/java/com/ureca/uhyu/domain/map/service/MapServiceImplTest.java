package com.ureca.uhyu.domain.map.service;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepository;
import com.ureca.uhyu.domain.store.repository.StoreRepositoryCustom;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Grade;
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
    class GetStoresInRangeTest {

        @Test
        void shouldReturnMappedStoreList() {
            // given
            double lat = 37.5;
            double lon = 127.0;
            double radius = 1000.0;

            when(storeRepositoryCustom.findStoresInRadius(lat, lon, radius))
                    .thenReturn(List.of(store));

            // when
            List<MapRes> result = mapService.getStoresInRange(lat, lon, radius);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).storeName()).isEqualTo("이디야 판교점");
        }
    }

    @Nested
    class GetCategoryStoreInRangeTest {

        @Test
        void shouldReturnStoresMatchingCategoryInRange() {
            // given
            double lat = 37.5;
            double lon = 127.0;
            double radius = 1000.0;
            String categoryName = "커피";

            when(storeRepositoryCustom.findCategoryStoresInRadius(lat, lon, radius, categoryName))
                    .thenReturn(List.of(store));

            // when
            List<MapRes> result = mapService.getCategoryStoreInRange(lat, lon, radius, categoryName);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).storeName()).isEqualTo("이디야 판교점");
        }
    }

    @Nested
    class GetBrandStoreInRangeTest {

        @Test
        void shouldReturnStoresMatchingBrandInRange() {
            // given
            double lat = 37.5;
            double lon = 127.0;
            double radius = 1000.0;
            String brandName = "이디야.";

            when(storeRepositoryCustom.findSearchedStoresInRadius(lat, lon, radius, brandName))
                    .thenReturn(List.of(store));

            // when
            List<MapRes> result = mapService.getSearchedStoresInRange(lat, lon, radius, brandName);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).storeName()).isEqualTo("이디야 판교점");
        }
    }

    @Nested
    class GetStoreDetailTest {

        @Test
        void shouldReturnBenefitMatchingUserGrade() {
            // given
            User user = mock(User.class);
            when(user.getGrade()).thenReturn(Grade.VIP);

            Benefit vipBenefit = Benefit.builder()
                    .grade(Grade.VIP)
                    .description("음료 2잔 무료")
                    .brand(brand)
                    .build();

            brand.setBenefits(List.of(vipBenefit));

            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

            // when
            StoreDetailRes res = mapService.getStoreDetail(1L, user);

            // then
            assertThat(res.benefits().benefitText()).isEqualTo("음료 2잔 무료");
            assertThat(res.usageLimit()).isEqualTo("1일 1회");
        }

        @Test
        void shouldReturnGoodBenefitIfNoExactMatch() {
            // given
            User user = mock(User.class);
            when(user.getGrade()).thenReturn(Grade.VIP); // fallback 케이스

            Benefit goodBenefit = Benefit.builder()
                    .grade(Grade.GOOD)
                    .description("1+1 혜택")
                    .brand(brand)
                    .build();

            brand.setBenefits(List.of(goodBenefit));

            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

            // when
            StoreDetailRes res = mapService.getStoreDetail(1L, user);

            // then
            assertThat(res.benefits().grade()).isEqualTo("GOOD");
            assertThat(res.benefits().benefitText()).isEqualTo("1+1 혜택");
        }

        @Test
        void shouldReturnNullIfNoBenefitExists() {
            // given
            User user = mock(User.class);
            when(user.getGrade()).thenReturn(Grade.VIP);

            brand.setBenefits(List.of()); // 혜택 없음

            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

            // when
            StoreDetailRes res = mapService.getStoreDetail(1L, user);

            // then
            assertThat(res.benefits()).isNull();
        }
    }
}
