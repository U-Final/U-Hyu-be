package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByBrandRes;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.CountFilterByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.UserBrandPair;
import com.ureca.uhyu.domain.user.entity.ActionLogs;
import com.ureca.uhyu.domain.user.enums.ActionType;
import com.ureca.uhyu.domain.user.repository.ActionLogsRepository;
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
    private static final int CATEGORY_ID_INDEX = 1;
    private static final int CATEGORY_NAME_INDEX = 2;
    private static final int BRAND_NAME_INDEX = 3;

    private final BookmarkRepository bookmarkRepository;
    private final ActionLogsRepository actionLogsRepository;

    public List<BookmarksByCategoryRes> findBookmarksByCategoryAndBrand() {
        Set<UserBrandPair> userBrandSaves = bookmarkRepository.findUserBrandSaves();
        Map<Long, Tuple> brandCategoryMap = bookmarkRepository.findBrandToCategoryMap();

        log.debug("Retrieved {} user-brand pairs and {} brand-category mappings",
                userBrandSaves.size(), brandCategoryMap.size());

        // 브랜드별 저장 수
        Map<Long, Integer> brandSaveCounts = userBrandSaves.stream()
                .collect(Collectors.groupingBy(
                        UserBrandPair::brandId,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        return aggregateBookmarksByCategory(brandSaveCounts, brandCategoryMap);
    }

    private List<BookmarksByCategoryRes> aggregateBookmarksByCategory(Map<Long, Integer> brandSaveCounts, Map<Long, Tuple> brandCategoryMap) {
        // 카테고리별 DTO 조립
        Map<Long, BookmarksByCategoryRes> categoryMap = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : brandSaveCounts.entrySet()) {
            Long brandId = entry.getKey();
            int count = entry.getValue();

            Tuple t = brandCategoryMap.get(brandId);
            if (t == null) continue;

            Long categoryId = t.get(CATEGORY_ID_INDEX, Long.class);
            String categoryName = t.get(CATEGORY_NAME_INDEX, String.class);
            String brandName = t.get(BRAND_NAME_INDEX, String.class);

            if (categoryId == null || categoryName == null || brandName == null) {
                log.warn("Invalid data in tuple for brandId: {}", brandId);
                continue;
            }

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

    public List<CountFilterByCategoryRes> findCountFilterByCategory() {
        return actionLogsRepository.findCountFilterByActionType(ActionType.FILTER_USED);
    }
}
