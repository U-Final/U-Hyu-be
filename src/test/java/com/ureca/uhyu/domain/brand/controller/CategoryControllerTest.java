//package com.ureca.uhyu.domain.brand.controller;
//
//import com.ureca.uhyu.domain.brand.dto.response.CategoryListRes;
//import com.ureca.uhyu.domain.brand.service.BrandService;
//import com.ureca.uhyu.domain.brand.service.CategoryService;
//import com.ureca.uhyu.global.config.TestSecurityConfig;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.times;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CategoryController.class)
//@Import(TestSecurityConfig.class)
//class CategoryControllerTest {
//
//    private static final String CATEGORY_URL = "/admin/categories";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CategoryService categoryService;
//
//    @MockBean
//    private BrandService brandService;
//
//    @BeforeEach
//    void initSecurity() {
//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken(
//                        "mockUser", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
//                )
//        );
//    }
//
//    @DisplayName("카테고리 목록 조회 요청은")
//    class Describe_getCategories {
//
//        @Test
//        @DisplayName("정상적인 경우 200과 카테고리 리스트를 반환한다")
//        void it_returns_category_list() throws Exception {
//            // given
//            List<CategoryListRes> mockCategories = List.of(
//                    new CategoryListRes(1L, "테마파크"),
//                    new CategoryListRes(2L, "쇼핑")
//            );
//
//            when(categoryService.getAllCategories()).thenReturn(mockCategories);
//
//            // when
//            ResultActions result = mockMvc.perform(get(CATEGORY_URL)
//                            .contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print());
//
//            // then
//            result.andExpect(status().isOk())
//                    .andExpect(jsonPath("$.data[0].categoryId").value(1))
//                    .andExpect(jsonPath("$.data[0].categoryName").value("테마파크"))
//                    .andExpect(jsonPath("$.data[1].categoryId").value(2))
//                    .andExpect(jsonPath("$.data[1].categoryName").value("쇼핑"));
//
//            verify(categoryService, times(1)).getAllCategories();
//        }
//    }
//}