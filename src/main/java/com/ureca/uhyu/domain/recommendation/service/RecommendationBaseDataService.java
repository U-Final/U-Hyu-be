package com.ureca.uhyu.domain.recommendation.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.recommendation.api.FastApiRecommendationClient;
import com.ureca.uhyu.domain.recommendation.dto.request.ExcludeBrandRequest;
import com.ureca.uhyu.domain.recommendation.entity.RecommendationBaseData;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationBaseDataRepository;
import com.ureca.uhyu.domain.user.dto.response.SaveUserInfoRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecommendationBaseDataService {

    private final BrandRepository brandRepository;
    private final RecommendationBaseDataRepository recommendationBaseDataRepository;
    private final FastApiRecommendationClient fastApiRecommendationClient;

    @Transactional
    public SaveUserInfoRes excludeBrand(User user, ExcludeBrandRequest request) {

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new GlobalException(ResultCode.BRAND_NOT_FOUND));

        // 이미 EXCLUDE로 등록된 경우 중복 저장 방지
        boolean exists = recommendationBaseDataRepository.existsByUserAndBrandAndDataType(user, brand, DataType.EXCLUDE);

        if (!exists) {
            RecommendationBaseData data = RecommendationBaseData.builder()
                    .user(user)
                    .brand(brand)
                    .dataType(DataType.EXCLUDE)
                    .build();
            recommendationBaseDataRepository.save(data);
        }

        // Fast API에 요청 재추천 로직 돌리게 하기 위해서 API 쏘기
        fastApiRecommendationClient.requestRecomputeRecommendation(user.getId());

        return SaveUserInfoRes.from(user);
    }
}
