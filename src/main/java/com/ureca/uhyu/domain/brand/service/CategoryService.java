package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.brand.dto.response.CategoryListRes;
import com.ureca.uhyu.domain.brand.repository.CategoryRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public List<CategoryListRes> getAllCategories() {
        try {
            return categoryRepository.findAll().stream()
                    .map(CategoryListRes::from)
                    .toList();
        } catch (Exception e) {
            throw new GlobalException(ResultCode.CATEGORY_NOT_FOUND);
        }
    }
}
