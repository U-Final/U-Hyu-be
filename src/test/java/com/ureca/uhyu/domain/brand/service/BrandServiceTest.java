package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.brand.dto.response.BrandInfoRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandNameRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandPageResult;
import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.brand.repository.CategoryRepository;
import com.ureca.uhyu.domain.user.enums.Grade;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BrandService brandService;

    @Mock
    private CategoryRepository categoryRepository;

    @DisplayName("제휴처 목록 조회 - 다음 페이지 존재(페이지네이션 검증)")
    @Test
    void findBrands_hasNextTrue() {
        // given
        String categoryName = "카페";
        List<String> storeTypes = List.of("ONLINE", "OFFLINE");
        List<String> benefitTypes = List.of("DISCOUNT", "GIFT");
        String brandName = "이디야";
        int page = 0;
        int size = 2;                   // 1 페이지당 2개씩

        Category category = createCategory(categoryName);
        setId(category, 1L);

        Brand brand1 = createBrand("이디야", "logo_A.png", category);
        Brand brand2 = createBrand("브랜드 B", "logo_B.png",  category);
        setId(brand1, 1L);
        setId(brand2, 2L);

        List<Brand> brandList = List.of(brand1, brand2);
        long totalCount = 5L;

        BrandPageResult mockResult = new BrandPageResult(brandList, totalCount);

        // mock
        when(brandRepository.findByCategoryOrNameOrTypes(categoryName, storeTypes, benefitTypes, brandName, page, size))
                .thenReturn(mockResult);

        // when
        BrandListRes result = brandService.findBrands(categoryName, storeTypes, benefitTypes, brandName, page, size);

        // then
        assertEquals(2, result.brandList().size());
        assertEquals(3, result.totalPages());
        assertEquals(brandName, result.brandList().get(0).brandName());
        assertEquals(page, result.currentPage());
        assertTrue(result.hasNext());
    }

    @DisplayName("브랜드 목록 조회 - 마지막 페이지(페이지네이션 검증)")
    @Test
    void findBrands_hasNextFalse() {
        // given
        String categoryName = "카페";
        List<String> storeTypes = List.of("ONLINE");
        List<String> benefitTypes = List.of("DISCOUNT");
        String brandName = null;
        int page = 1;
        int size = 2;

        Category category = createCategory(categoryName);
        setId(category, 1L);

        Brand brand1 = createBrand("이디야", "logo_A.png", category);
        setId(brand1, 3L);
        List<Brand> brandList = List.of(brand1); // 마지막 페이지에 1건만

        long totalCount = 3L;

        BrandPageResult mockResult = new BrandPageResult(brandList, totalCount);

        // mock
        when(brandRepository.findByCategoryOrNameOrTypes(categoryName, storeTypes, benefitTypes, brandName, page, size))
                .thenReturn(mockResult);

        // when
        BrandListRes result = brandService.findBrands(categoryName, storeTypes, benefitTypes, brandName, page, size);

        // then
        assertEquals(1, result.brandList().size());
        assertEquals(2, result.totalPages()); // (int) ceil(3 / 2) = 2
        assertFalse(result.hasNext());
        assertEquals(page, result.currentPage());
    }

    @DisplayName("브랜드 목록 조회 - 결과 없음")
    @Test
    void findBrands_emptyResult() {
        // given
        String category = "뷰티";
        List<String> storeTypes = List.of("ONLINE");
        List<String> benefitTypes = List.of("DISCOUNT");
        String brandName = "없는브랜드";
        int page = 0;
        int size = 10;

        BrandPageResult mockResult = new BrandPageResult(List.of(), 0L);

        // mock
        when(brandRepository.findByCategoryOrNameOrTypes(category, storeTypes, benefitTypes, brandName, page, size))
                .thenReturn(mockResult);

        // when
        BrandListRes result = brandService.findBrands(category, storeTypes, benefitTypes, brandName, page, size);

        // then
        assertTrue(result.brandList().isEmpty());
        assertEquals(0, result.totalPages());
        assertFalse(result.hasNext());
        assertEquals(page, result.currentPage());
    }

    @DisplayName("브랜드 상세 조회 - 성공")
    @Test
    void findBrandInfo_success() {
        // given
        Category category = createCategory("카페");
        setId(category, 1L);

        Brand brand = createBrand("스타벅스", "starbucks.png", category);
        setId(brand, 10L);

        when(brandRepository.findById(10L)).thenReturn(Optional.of(brand));

        // when
        BrandInfoRes result = brandService.findBrandInfo(10L);

        // then
        assertEquals("스타벅스", result.brandName());
        assertEquals("starbucks.png", result.logoImage());
    }

    @DisplayName("브랜드 상세 조회 - 실패 (존재하지 않는 브랜드 ID)")
    @Test
    void findBrandInfo_BrandNotFound() {
        // given
        when(brandRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            brandService.findBrandInfo(999L);
        });

        assertEquals(ResultCode.BRAND_NOT_FOUND, exception.getResultCode());
    }

    @DisplayName("카테고리 ID로 브랜드 목록 조회 - 성공")
    @Test
    void findBrandByCategoryId_success() {
        // given
        Long categoryId = 1L;
        Category category = createCategory("패션");
        setId(category, categoryId);

        Brand brand1 = createBrand("브랜드A", "a.png", category);
        setId(brand1, 10L);
        Brand brand2 = createBrand("브랜드B", "b.png", category);
        setId(brand2, 11L);
        List<Brand> brands = List.of(brand1, brand2);

        // mock
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(brandRepository.findByCategory(category)).thenReturn(brands);

        // when
        List<BrandNameRes> result = brandService.findBrandByCategoryId(categoryId);

        // then
        assertEquals(2, result.size());
        assertEquals("브랜드A", result.get(0).brandName());
        assertEquals("브랜드B", result.get(1).brandName());
        verify(categoryRepository).findById(categoryId);
        verify(brandRepository).findByCategory(category);
    }

    @DisplayName("카테고리 ID로 브랜드 목록 조회 - 존재하지 않는 카테고리")
    @Test
    void findBrandByCategoryId_notFoundCategory() {
        // given
        Long categoryId = 999L;

        // mock
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            brandService.findBrandByCategoryId(categoryId);
        });

        assertEquals(ResultCode.NOT_FOUND_CATEGORY, exception.getResultCode());
        verify(categoryRepository).findById(categoryId);
        verify(brandRepository, never()).findByCategory(any());
    }

    private Category createCategory(String name) {
        return Category.builder()
                .categoryName(name)
                .build();
    }

    private Brand createBrand(String name, String image,  Category category) {
        Benefit benefit = Benefit.builder()
                .description("혜택1")
                .grade(Grade.GOOD)
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

    private void setId(Object target, Long idValue) {
        try {
            Field idField = target.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(target, idValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("브랜드 삭제 성공")
    @Test
    void deleteBrand_success() {
        // given
        Long categoryId = 1L;
        Category category = createCategory("패션");
        setId(category, categoryId);

        Brand brand = createBrand("이디야", "img.png", category);
        setId(brand, 5L);
        when(brandRepository.findByIdAndDeletedFalse(5L)).thenReturn(Optional.of(brand));

        // when
        brandService.deleteBrand(5L);

        // then
        assertTrue(brand.getDeleted()); // soft delete 확인
    }
}
