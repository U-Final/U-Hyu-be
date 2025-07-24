package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByBrandRes;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.UserBrandPair;
import com.ureca.uhyu.domain.user.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final BookmarkRepository bookmarkRepository;

    public List<BookmarksByCategoryRes> findBookmarksByCategoryAndBrand() {
        Set<UserBrandPair> userBrandSaves = bookmarkRepository.findUserBrandSaves();
        Map<Long, Tuple> brandCategoryMap = bookmarkRepository.findBrandToCategoryMap();

        // 브랜드별 저장 수
        Map<Long, Integer> brandSaveCounts = userBrandSaves.stream()
                .collect(Collectors.groupingBy(
                        UserBrandPair::brandId,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        // 카테고리별 DTO 조립
        Map<Long, BookmarksByCategoryRes> categoryMap = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : brandSaveCounts.entrySet()) {
            Long brandId = entry.getKey();
            int count = entry.getValue();

            Tuple t = brandCategoryMap.get(brandId);
            if (t == null) continue;

            Long categoryId = t.get(1, Long.class);
            String categoryName = t.get(2, String.class);
            String brandName = t.get(3, String.class);

            BookmarksByBrandRes brandRes = BookmarksByBrandRes.of(brandName, count);

            categoryMap.compute(categoryId, (key, existing) -> {
                if (existing == null) {
                    List<BookmarksByBrandRes> brandList = new ArrayList<>();
                    brandList.add(brandRes);
                    return BookmarksByCategoryRes.of(categoryId, categoryName, count, brandList);
                } else {
                    existing.bookmarksByBrandList().add(brandRes);
                    int newSum = existing.sumBookmarksByCategory() + count;
                    return BookmarksByCategoryRes.of(categoryId, categoryName, newSum, existing.bookmarksByBrandList());
                }
            });
        }

        return new ArrayList<>(categoryMap.values());
    }
}
