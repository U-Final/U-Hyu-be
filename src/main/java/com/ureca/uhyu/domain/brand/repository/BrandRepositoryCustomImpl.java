package com.ureca.uhyu.domain.brand.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.uhyu.domain.brand.dto.response.BrandPageResult;
import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.QBenefit;
import com.ureca.uhyu.domain.brand.entity.QBrand;
import com.ureca.uhyu.domain.brand.entity.QCategory;
import com.ureca.uhyu.domain.brand.enums.BenefitType;
import com.ureca.uhyu.domain.brand.enums.StoreType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryCustomImpl implements BrandRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public BrandPageResult findByCategoryOrNameOrTypes(String category,
                                                       String brandName,
                                                       List<String> storeType,
                                                       List<String> benefitType,
                                                       int page,
                                                       int size) {

        QBrand brand = QBrand.brand;
        QCategory categoryEntity = QCategory.category;
        QBenefit benefit = QBenefit.benefit;

        BooleanBuilder builder = new BooleanBuilder();

        // 브랜드 이름 조건
        if (StringUtils.hasText(brandName)) {
            builder.and(brand.brandName.containsIgnoreCase(brandName));
        }

        // 카테고리 조건
        if (StringUtils.hasText(category) && !"all".equalsIgnoreCase(category)) {
            builder.and(brand.category.categoryName.eq(category));
        }

        // 매장 타입 필터
        if (storeType != null && !storeType.isEmpty()) {
            List<StoreType> storeTypes = storeType.stream()
                    .map(String::toUpperCase)
                    .map(StoreType::valueOf)
                    .toList();
            builder.and(brand.storeType.in(storeTypes));
        }

        // 혜택 타입 필터
        if (benefitType != null && !benefitType.isEmpty()) {
            List<BenefitType> benefitTypes = benefitType.stream()
                    .map(String::toUpperCase)
                    .map(BenefitType::valueOf)
                    .toList();
            builder.and(benefit.benefitType.in(benefitTypes));
        }

        Pageable pageable = PageRequest.of(page, size);

        // 전체 개수 조회
        Long count = queryFactory
                .select(brand.countDistinct())
                .from(brand)
                .join(brand.category, categoryEntity)
                .leftJoin(brand.benefits, benefit)
                .where(builder)
                .fetchOne();
        long totalCount = Optional.ofNullable(count).orElse(0L);

        // 결과 리스트 조회
        List<Brand> result = queryFactory
                .selectFrom(brand)
                .distinct()
                .join(brand.category, categoryEntity)
                .leftJoin(brand.benefits, benefit)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new BrandPageResult(result, totalCount);
    }
}
