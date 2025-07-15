package com.ureca.uhyu.domain.brand.service;

import com.ureca.uhyu.domain.brand.dto.response.BrandListRes;
import com.ureca.uhyu.domain.brand.dto.response.BrandRes;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandListRes findBrands(String category, List<String> tag1, List<String> tag2, String brandName, int page, int size) {
        List<Brand> brand = brandRepository.findByCategoryOrNameOrTypes(category, brandName, tag1, tag2, page, size);
        List<BrandRes> brandList = new ArrayList<>();
        for(Brand b : brand){
            brandList.add(BrandRes.from(b));
        }

        //TODO : 빌드하려고 임시로 빨간줄만 안뜨게 만듦, 수정 예정
        boolean hasNext = brandList.size() < size;
        return BrandListRes.from(brandList, hasNext);
    }
}
