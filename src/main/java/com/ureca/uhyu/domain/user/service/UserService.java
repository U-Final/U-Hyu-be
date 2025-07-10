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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final RecommendationBaseDataRepository recommendationBaseDataRepository;

    @Transactional
    public Long saveOnboardingInfo(UserOnboardingRequest request) {
        // 현재 로그인 유저 ID 추출 (예시: SecurityContext 등)
        Long currentUserId = getCurrentUserId();

        // 1. 유저 조회
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_USER));

        // 2. 온보딩 정보 반영
        user.setUserGrade(request.grade());
        user.setUserRole(UserRole.USER); // TMP_USER → USER 변경

//       최근 이용 브랜드 저장
        saveUserBrandData(user, request.recentBrands(), DataType.RECENT);

        // 관심 브랜드 저장
        saveUserBrandData(user, request.interestedBrands(), DataType.INTEREST);

        return user.getId();
    }

    private Long getCurrentUserId() {
        // TODO: JWT 또는 SecurityContext에서 userId 추출하는 실제 로직 구현
        return 1L; // 테스트용 ID (임시)
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