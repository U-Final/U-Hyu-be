package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.repository.BrandRepository;
import com.ureca.uhyu.domain.recommendation.entity.RecommendationBaseData;
import com.ureca.uhyu.domain.recommendation.enums.DataType;
import com.ureca.uhyu.domain.recommendation.repository.RecommendationBaseDataRepository;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.dto.response.UpdateUserRes;
import com.ureca.uhyu.domain.user.entity.Marker;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.repository.MarkerRepository;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RecommendationBaseDataRepository recommendationRepository;
    private final BrandRepository brandRepository;
    private final MarkerRepository markerRepository;

    public GetUserInfoRes findUserInfo(User user) {
        return GetUserInfoRes.from(user);
    }

    @Transactional
    public UpdateUserRes updateByUser(User user, UpdateUserReq request) {

        if (request.updatedProfileImage() != null) {
            user.updateProfileImage(request.updatedProfileImage());
        }

        if (request.updatedNickName() != null) {
            user.updateNickName(request.updatedNickName());
        }

        if (request.markerId() != null) {
            Marker marker = markerRepository.findById(request.markerId())
                    .orElseThrow(() -> new GlobalException(ResultCode.INVALID_INPUT));      //임시로 넣은 에러코드
            user.updateMarker(marker);
        }

        if (request.updatedBrandIdList() != null && !request.updatedBrandIdList().isEmpty()) {
            recommendationRepository.deleteByUserAndDataType(user, DataType.INTEREST);

            for (Long brandId : request.updatedBrandIdList()) {
                Brand brand = brandRepository.findById(brandId)
                        .orElseThrow(() -> new GlobalException(ResultCode.INVALID_BRAND));

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
}
