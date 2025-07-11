package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.recommendation.entity.RecommendationBaseData;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationBaseDataRepository;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingRequest;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ureca.uhyu.domain.auth.dto.CustomOAuth2User;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final RecommendationBaseDataRepository recommendationBaseDataRepository;

    @Transactional
    public Long saveOnboardingInfo(UserOnboardingRequest request) {

        Long currentUserId = getCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));

        user.setUserGrade(request.grade());
        user.setUserRole(UserRole.USER); // TMP_USER → USER 변경

        saveUserBrandData(user, request.recentBrands(), DataType.RECENT);
        saveUserBrandData(user, request.interestedBrands(), DataType.INTEREST);

        return user.getId();
    }

    private Long getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> auth.getPrincipal())
                .filter(principal -> principal instanceof CustomOAuth2User)
                .map(principal -> ((CustomOAuth2User) principal).getUserId())
                .orElseThrow(() -> new GlobalException(ResultCode.UNAUTHORIZED));
    }

    public GetUserInfoRes findUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));
        return GetUserInfoRes.from(user);
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

        recommendationBaseDataRepository.saveAll(dataList);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));
    }
}