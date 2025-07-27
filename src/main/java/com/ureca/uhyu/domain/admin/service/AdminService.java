package com.ureca.uhyu.domain.admin.service;

import com.querydsl.core.Tuple;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByBrandRes;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.StatisticsFilterByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.CountFilterByCategoryRes;
import com.ureca.uhyu.domain.admin.dto.response.BookmarksByBrand;
import com.ureca.uhyu.domain.admin.dto.response.CountBookmarkRes;
import com.ureca.uhyu.domain.admin.dto.response.CountRecommendationRes;
import com.ureca.uhyu.domain.admin.dto.response.UserBrandPair;
import com.ureca.uhyu.domain.user.repository.actionLogs.ActionLogsRepository;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import com.ureca.uhyu.domain.user.enums.ActionType;
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
    private final RecommendationRepository recommendationRepository;

    public List<CountBookmarkRes> findBookmarksByCategoryAndBrand() {
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

    private List<CountBookmarkRes> aggregateBookmarksByCategory(Map<Long, Integer> brandSaveCounts, Map<Long, Tuple> brandCategoryMap) {
        // 카테고리별 DTO 조립
        Map<Long, CountBookmarkRes> categoryMap = new LinkedHashMap<>();

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

            BookmarksByBrand brandRes = BookmarksByBrand.of(brandName, count);

            categoryMap.compute(categoryId, (key, existing) -> {
                if (existing == null) {
                    List<BookmarksByBrand> brandList = new ArrayList<>();
                    brandList.add(brandRes);
                    return CountBookmarkRes.of(categoryId, categoryName, count, brandList);
                } else {
                    existing.bookmarksByBrandList().add(brandRes);
                    int newSum = existing.sumBookmarksByCategory() + count;
                    return CountBookmarkRes.of(categoryId, categoryName, newSum, existing.bookmarksByBrandList());
                }
            });
        }

        return new ArrayList<>(categoryMap.values());
    }

    public List<StatisticsFilterByCategoryRes> findStatisticsFilterByCategory() {
        return actionLogsRepository.findStatisticsFilterByActionType(ActionType.FILTER_USED);
    }

    public List<CountRecommendationRes> findCountRecommendationByCategoryAndBrand() {
        return recommendationRepository.findCountRecommendationByCategory();
    }
}
