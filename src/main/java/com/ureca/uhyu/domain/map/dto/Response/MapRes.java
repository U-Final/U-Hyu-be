package com.ureca.uhyu.domain.map.dto.response;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.store.entity.Store;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지도 제휴매장 정보 응답 DTO")
public record MapRes(
        @Schema(description = "매장 ID")
        Long storeId,

        @Schema(description = "매장 이름")
        String storeName,

        @Schema(description = "카테고리 이름")
        String categoryName,

        @Schema(description = "매장 상세주소")
        String addressDetail,

        @Schema(description = "혜택")
        String benefit,

        @Schema(description = "로고 이미지")
        String logo_image,

        @Schema(description = "브랜드 이름")
        String brandName,

        @Schema(description = "위도")
        Double latitude,

        @Schema(description = "경도")
        Double longitude
) {
        public static MapRes from(Store store) {
                Brand brand = store.getBrand();
                Category category = brand.getCategory();

                // TODO : 헤택(Benefit) 회원 등급별로 표시되는건지 확인 후 로직 수정
                String benefit = null;

                return new MapRes(
                        store.getId(),
                        store.getName(),
                        category.getCategoryName(),
                        store.getAddrDetail(),
                        benefit,
                        brand.getLogoImage(),
                        store.getBrand().getBrandName(),
                        store.getGeom().getY(),
                        store.getGeom().getX()
                );
        }
}
