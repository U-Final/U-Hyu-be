package com.ureca.uhyu.domain.map.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.map.dto.Request.MapReq;
import com.ureca.uhyu.domain.map.dto.Response.MapRes;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepositoryCustom;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapServiceImplTest {

    @Mock
    private StoreRepositoryCustom storeRepository;

    @InjectMocks
    private MapServiceImpl mapService;

    @Nested
    class WhenValidCoordinatesAreGiven {

        @Test
        void shouldReturnMappedStoreList() {
            // given
            double lat = 37.5;
            double lon = 127.0;
            double radius = 1000.0;

            MapReq request = new MapReq(lat, lon, radius);


            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(lon, lat));

            Category category = Category.builder()
                    .categoryName("카페")
                    .build();

            Brand brand = Brand.builder()
                    .brandName("이디야")
                    .logoImage("logo.jpg")
                    .category(category)
                    .build();

            Store store1 = Store.builder()
                    .id(1L)
                    .name("Test Store1")
                    .addrDetail("성남시 분당구")
                    .brand(brand)
                    .geom(point)
                    .build();

            Store store2 = Store.builder()
                    .id(1L)
                    .name("Test Store2")
                    .addrDetail("서울시 강남구")
                    .brand(brand)
                    .geom(point)
                    .build();

            when(storeRepository.findStoresWithBrandAndBenefitWithinRadius(lat, lon, radius))
                    .thenReturn(List.of(store1,store2));

            // when
            List<MapRes> result = mapService.getStoresInRange(request);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(1).storeName()).isEqualTo("Test Store2");
        }
    }
}
