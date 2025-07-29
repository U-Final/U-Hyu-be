package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.brand.dto.response.BrandNameRes;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.brand.repository.CategoryRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    BrandService brandService;

    @DisplayName("카테고리 ID로 브랜드 목로 조회 - 성공")
    @Test
    void findByBrandByCategoryId(){

        // given
        Long categoryId = 1L;
        Category category = createCategory("패션");
        setId(category, categoryId);

        Brand brand1 = createBrand("브랜드A", "a.png", category);
        setId(brand1, 10L);
        Brand brand2 = createBrand("브랜드B", "b.png", category);
        setId(brand2, 11L);
        List<Brand> brands = List.of(brand1, brand2);

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

    private Brand createBrand(String name, String image, Category category) {
        return Brand.builder()
                .brandName(name)
                .logoImage(image)
                .category(category)
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