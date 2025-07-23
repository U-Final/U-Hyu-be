package com.ureca.uhyu.domain.brand.controller;

import com.ureca.uhyu.domain.brand.dto.response.CategoryListRes;
import com.ureca.uhyu.domain.brand.service.CategoryService;
import com.ureca.uhyu.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public CommonResponse<List<CategoryListRes>> getCategories() {
        return CommonResponse.success(categoryService.getAllCategories());
    }
}
