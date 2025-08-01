package com.ureca.uhyu.domain.map.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.map.dto.response.MapBookmarkRes;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationRepository;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepository;
import com.ureca.uhyu.domain.store.repository.StoreRepositoryCustom;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkListRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final StoreRepositoryCustom storeRepositoryCustom;
    private final StoreRepository storeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkListRepository bookmarkListRepository;
    private final RecommendationRepository recommendationRepository;

    @Override
    public List<MapRes> getFilteredStores(Double lat, Double lon, Double radius, String categoryName, String brandName) {
        List<Store> stores = storeRepositoryCustom.findStoresByFilters(lat, lon, radius, categoryName, brandName);
        return stores.stream()
                .map(MapRes::from)
                .toList();
    }

    @Override
    public List<MapRes> getBookmarkedStores(User user) {
        BookmarkList bookmarkList = bookmarkListRepository.findByUser(user)
                .orElseThrow(() -> new GlobalException(ResultCode.BOOKMARK_LIST_NOT_FOUND));

        List<Bookmark> bookmarks = bookmarkRepository.findByBookmarkList(bookmarkList);

        return bookmarks.stream()
                .map(Bookmark::getStore)
                .map(store -> MapRes.from(store, user))
                .toList();
    }

    @Override
    public StoreDetailRes getStoreDetail(Long storeId, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_STORE));

        Brand brand = store.getBrand();
        Grade userGrade = user.getGrade();

        String benefitDescription = brand.getBenefitDescriptionByGradeOrDefault(userGrade);

        StoreDetailRes.BenefitDetail benefitDetail = new StoreDetailRes.BenefitDetail(
                userGrade.name(),
                benefitDescription
        );

        boolean isFavorite = bookmarkRepository.existsByBookmarkListUserAndStore(user, store);
        int favoriteCount = bookmarkRepository.countByStore(store);

        return new StoreDetailRes(
                store.getName(),
                isFavorite,
                favoriteCount,
                benefitDetail,
                brand.getUsageLimit(),
                brand.getUsageMethod()
        );
    }

    @Transactional
    @Override
    public MapBookmarkRes toggleBookmark(User user, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_STORE));

        BookmarkList bookmarkList = bookmarkListRepository.findByUser(user)
                .orElseGet(() -> bookmarkListRepository.save(BookmarkList.builder().user(user).build()));

        boolean isBookmarked;
        Optional<Bookmark> bookmarkOpt = bookmarkRepository.findByBookmarkListAndStore(bookmarkList, store);

        if(bookmarkOpt.isPresent()){
            bookmarkRepository.delete(bookmarkOpt.get());
            isBookmarked = false;
        }else{
            bookmarkRepository.save(new Bookmark(bookmarkList, store));
            isBookmarked = true;
        }

        return new MapBookmarkRes(storeId,isBookmarked);
    }

    @Override
    public List<MapRes> findRecommendedStores(Double lat, Double lon, Double radius, User user) {

        // 가장 최근 추천 받은 브랜드들 중 top3 추천 브랜드 가져오기
        List<Long> brandIds = recommendationRepository.findTop3ByUserOrderByCreatedAtDescRankAsc(user.getId())
                .stream()
                .map(r -> {
                    if (r.getBrand() == null) {
                        throw new GlobalException((ResultCode.BRAND_ID_IS_NULL));
                    }
                    return r.getBrand().getId();
                })
                .toList();

        if (brandIds.isEmpty()) {
            return List.of();
        }

        //추천 받은 브랜드들의 매장 목록 가져오기
        List<Store> stores = storeRepositoryCustom.findStoresByBrandAndRadius(lat, lon, radius, brandIds);

        return stores.stream()
                .map(store -> MapRes.from(store,user))
                .toList();
    }
}
