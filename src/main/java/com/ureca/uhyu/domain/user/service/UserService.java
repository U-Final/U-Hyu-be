package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingRequest;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.enums.UserRole;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;

    @Transactional
    public Long saveOnboardingInfo(UserOnboardingRequest request) {
        // 현재 로그인 유저 ID 추출 (예시: SecurityContext 등)
        Long currentUserId = getCurrentUserId();

        // 1. 유저 조회
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        // 2. 온보딩 정보 반영
        user.setGrade(request.grade());
        user.setRole(UserRole.USER); // TMP_USER → USER 변경

        // 3. 최근 이용 브랜드/관심 브랜드 저장 (연관 관계 처리 필요)
        brandRepository.saveAllForUser(user, request.recentBrands(), true);
        brandRepository.saveAllForUser(user, request.interestedBrands(), false);

        return user.getId();
    }

    private Long getCurrentUserId() {
        // TODO: JWT 또는 SecurityContext에서 userId 추출하는 실제 로직 구현
        return 1L; // 테스트용 ID (임시)
    }
}