package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.recommendation.entity.RecommendationBaseData;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationBaseDataRepository;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepository;
import com.ureca.uhyu.domain.user.dto.request.ActionLogsReq;
import com.ureca.uhyu.domain.user.dto.request.SaveRecentVisitReq;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingReq;
import com.ureca.uhyu.domain.user.dto.response.*;
import com.ureca.uhyu.domain.user.entity.*;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.*;
import com.ureca.uhyu.domain.user.repository.actionLogs.ActionLogsRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkListRepository;
import com.ureca.uhyu.domain.user.repository.bookmark.BookmarkRepository;
import com.ureca.uhyu.domain.user.repository.history.HistoryRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final RecommendationBaseDataRepository recommendationRepository;
    private final BookmarkListRepository bookmarkListRepository;
    private final BookmarkRepository bookmarkRepository;
    private final HistoryRepository historyRepository;
    private final ActionLogsRepository actionLogsRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Long saveOnboardingInfo(UserOnboardingReq request, User user) {

        User persistedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));

        String ageRange = getAgeRange(request.age());

        persistedUser.updateUserInfo(
                request.age(),
                request.gender(),
                ageRange,
                request.grade(),
                UserRole.USER
        );
        userRepository.save(persistedUser);

        // 방문 브랜드는 history 테이블에 저장 - store_id는 null
        List<Long> brandIds = request.recentBrands();

        for (Long brandId : brandIds) {
            Brand brand = brandRepository.findById(brandId)
                    .orElseThrow(() -> new GlobalException(ResultCode.INVALID_INPUT));

            saveHistory(persistedUser, brand, null, true);
        }

        // 관심 브랜드는 recommendation_base_data 테이블에 저장
        saveUserBrandData(persistedUser, request.interestedBrands(), DataType.INTEREST);

        //온보딩 시 해당 user에 대한 즐겨찾기 List도 생성
        if (bookmarkListRepository.existsByUser(persistedUser)) {
            throw new GlobalException(ResultCode.BOOKMARK_LIST_ALREADY_EXISTS);
        }
        else {
            BookmarkList bookmarkList = BookmarkList.builder()
                    .user(persistedUser)
                    .build();
            bookmarkListRepository.save(bookmarkList);
        }

        return persistedUser.getId();
    }

    public String getAgeRange(Integer age) {
        String ageRange = "";

        if (age != null) {
            int lowerBound = (age / 10) * 10;
            int upperBound = lowerBound + 9;
            ageRange = lowerBound + "~" + upperBound;
        }
        return ageRange;
    }

    private void saveHistory(User user, Brand brand, Store store, Boolean isOnboarding) {
        int benefitPrice = (isOnboarding != null && isOnboarding)
                ? 0
                : switch (user.getGrade()) {
                    case GOOD -> 500;
                    case VIP -> 1000;
                    case VVIP -> 1500;
                    default -> 0;
                };

        History history = History.builder()
                .user(user)
                .brand(brand)
                .store(store)
                .benefitPrice(benefitPrice)
                .visitedAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }

    public GetUserInfoRes findUserInfo(User user) {
        List<RecommendationBaseData> recommendationBaseDataList = recommendationRepository.findByUser(user);
        List<Brand> brandList = recommendationBaseDataList.stream().map(RecommendationBaseData::getBrand).toList();
        return GetUserInfoRes.from(user, brandList);
    }

    @Transactional
    public UpdateUserRes updateUserInfo(User user, UpdateUserReq request) {
        String image = (request.updatedProfileImage() != null)?
                request.updatedProfileImage():user.getProfileImage();

        String nickname = (request.updatedNickName() != null)?
                request.updatedNickName():user.getNickname();

        Grade grade = (request.updatedGrade() != null)?
                request.updatedGrade():user.getGrade();

        user.updateUser(image, nickname, grade);

        if (request.updatedBrandIdList() != null && !request.updatedBrandIdList().isEmpty()) {
            recommendationRepository.deleteByUserAndDataType(user, DataType.INTEREST);

            for (Long brandId : request.updatedBrandIdList()) {
                Brand brand = brandRepository.findById(brandId)
                        .orElseThrow(() -> new GlobalException(ResultCode.INVALID_INPUT));

                RecommendationBaseData newInterest = RecommendationBaseData.builder()
                        .user(user)
                        .brand(brand)
                        .dataType(DataType.INTEREST)
                        .build();

                recommendationRepository.save(newInterest);
            }
        }

        User savedUser = userRepository.save(user);
        return UpdateUserRes.from(savedUser);
    }

    private void saveUserBrandData(User user, List<Long> brandIds, DataType dataType) {
        List<Brand> brands = brandRepository.findAllById(brandIds);

        if (brands.size() != brandIds.size()) {
            throw new GlobalException(ResultCode.INVALID_INPUT); // 일부 브랜드가 존재하지 않음
        }

        List<RecommendationBaseData> dataList = brands.stream()
                .map(brand -> RecommendationBaseData.builder()
                        .user(user)
                        .brand(brand)
                        .dataType(dataType)
                        .build())
                .toList();

        recommendationRepository.saveAll(dataList);
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public void validateEmailAvailability(User currentUser, String email) {
        if (!currentUser.getEmail().equals(email) && isEmailDuplicate(email)) {
            throw new GlobalException(ResultCode.EMAIL_DUPLICATED);
        }
    }

    public List<BookmarkRes> findBookmarkList(User user) {
        BookmarkList bookmarkList = bookmarkListRepository.findByUser(user)
                .orElseThrow(() -> new GlobalException(ResultCode.BOOKMARK_LIST_NOT_FOUND));
        List<Bookmark> bookmarks = bookmarkRepository.findByBookmarkList(bookmarkList);

        return bookmarks.stream()
                .map(BookmarkRes::from)
                .toList();
    }

    @Transactional
    public void deleteBookmark(User user, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new GlobalException(ResultCode.BOOKMARK_NOT_FOUND));

        if (!bookmark.getBookmarkList().getUser().getId().equals(user.getId())) {
            throw new GlobalException(ResultCode.FORBIDDEN);
        }

        bookmarkRepository.delete(bookmark);
    }

    public UserStatisticsRes findUserStatistics(User user) {
        Integer discountMoney = historyRepository.findDiscountMoneyThisMonth(user);;
        List<Brand> brands = actionLogsRepository.findTop3ClickedBrands(user);
        List<BestBrandListRes> bestBrandListRes = brands.stream()
                .map(BestBrandListRes::from)
                .toList();
        List<Store> stores = historyRepository.findRecentStoreInMonth(user);
        List<RecentStoreListRes> recentStoreListRes = stores.stream()
                .map(RecentStoreListRes::from)
                .toList();
        return UserStatisticsRes.from(discountMoney, bestBrandListRes, recentStoreListRes);
    }

    @Transactional
    public SaveUserInfoRes saveActionLogs(User user, ActionLogsReq request) {
        if (request.storeId() == null && request.categoryId() == null) {
            throw new GlobalException(ResultCode.INVALID_INPUT);
        }

        ActionLogs actionLogs = ActionLogs.builder()
                .user(user)
                .actionType(request.actionType())
                .storeId(request.storeId())
                .categoryId(request.categoryId())
                .build();

        actionLogsRepository.save(actionLogs);

        return SaveUserInfoRes.from(user);
    }

    @Transactional
    public SaveUserInfoRes saveVisitedBrand(User user, SaveRecentVisitReq request) {
        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new GlobalException(ResultCode.STORE_NOT_FOUND));

        saveHistory(user, store.getBrand(), store, false);

        return SaveUserInfoRes.from(user);
    }
}
