package com.ureca.uhyu.domain.map.service;

import com.ureca.uhyu.domain.brand.entity.Benefit;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.map.dto.response.MapBookmarkRes;
import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.store.dto.response.StoreDetailRes;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepository;
import com.ureca.uhyu.domain.store.repository.StoreRepositoryCustom;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.repository.BookmarkListRepository;
import com.ureca.uhyu.domain.user.repository.BookmarkRepository;
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

    @Override
    public List<MapRes> getFilteredStores(Double lat, Double lon, Double radius, String categoryName, String brandName) {
        List<Store> stores = storeRepositoryCustom.findStoresByFilters(lat, lon, radius, categoryName, brandName);
        return stores.stream()
                .map(MapRes::from)
                .toList();
    }

    @Override
    public StoreDetailRes getStoreDetail(Long storeId, User user) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_STORE));

        Brand brand = store.getBrand();
        Grade userGrade = user.getGrade();

        List<Benefit> benefits = brand.getBenefits();

        // 1. 유저 등급과 일치하는 혜택 먼저 찾기
        Optional<Benefit> matchingBenefit = brand.getBenefits().stream()
                .filter(b -> b.getGrade() == userGrade)
                .findFirst();

        // 2. 없으면 GOOD 혜택 fallback
        Benefit selected = matchingBenefit.orElseGet(() ->
                brand.getBenefits().stream()
                        .filter(b -> b.getGrade() == Grade.GOOD)
                        .findFirst()
                        .orElse(null)
        );

        StoreDetailRes.BenefitDetail benefitDetail = null;
        if (selected != null) {
            benefitDetail = new StoreDetailRes.BenefitDetail(
                    selected.getGrade().name(),
                    selected.getDescription()
            );
        }

        return new StoreDetailRes(
                store.getName(),
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
}
