package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.recommendation.entity.RecommendationBaseData;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationBaseDataRepository;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingRequest;
import com.ureca.uhyu.domain.user.dto.response.*;
import com.ureca.uhyu.domain.user.entity.*;
import com.ureca.uhyu.domain.user.enums.Grade;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.*;
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
public class UserService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final RecommendationBaseDataRepository recommendationRepository;
    private final MarkerRepository markerRepository;
    private final BookmarkListRepository bookmarkListRepository;
    private final BookmarkRepository bookmarkRepository;
    private final HistoryRepository historyRepository;
    private final ActionLogsRepository actionLogsRepository;


    @Transactional
    public Long saveOnboardingInfo(UserOnboardingRequest request, User user) {

        user.setUserGrade(request.grade());
        user.setUserRole(UserRole.USER); // TMP_USER → USER 변경

        saveUserBrandData(user, request.recentBrands(), DataType.RECENT);
        saveUserBrandData(user, request.interestedBrands(), DataType.INTEREST);

        return user.getId();
    }

    public GetUserInfoRes findUserInfo(User user) {
        return GetUserInfoRes.from(user);
    }

    @Transactional
    public UpdateUserRes updateUserInfo(User user, UpdateUserReq request) {
        String image = (request.updatedProfileImage() != null)?
                request.updatedProfileImage():user.getProfileImage();

        String nickname = (request.updatedNickName() != null)?
                request.updatedNickName():user.getNickname();

        Grade grade = (request.updatedGrade() != null)?
                request.updatedGrade():user.getGrade();

        Marker marker = (request.markerId() != null)?
                markerRepository.findById(request.markerId())
                        .orElseThrow(() -> new GlobalException(ResultCode.INVALID_INPUT)):user.getMarker();

        user.updateUser(image, nickname, grade, marker);

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

    private void saveUserBrandData(User user, List<String> brandNames, DataType dataType) {
        List<Brand> brands = brandRepository.findByBrandNameIn(brandNames);

        if (brands.size() != brandNames.size()) {
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
        List<Store> stores = historyRepository.findTop3RecentStore(user);
        List<RecentStoreListRes> recentStoreListRes = stores.stream()
                .map(RecentStoreListRes::from)
                .toList();
        return UserStatisticsRes.from(discountMoney, bestBrandListRes, recentStoreListRes);
    }
}
