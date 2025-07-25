package com.ureca.uhyu.domain.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "카테고리, 브랜드별 멤버십 사용횟수 통계 응답 DTO")
public record CountMembershipUsageRes(
        Long categoryId,
        String categoryName,
        Integer sumMembershipUsageByCategory,
        List<MembershipUsageByBrand> membershipUsageByBrandList
){
    public static CountMembershipUsageRes of (Long categoryId, String categoryName
            , Integer sumMembershipUsageByCategory, List<MembershipUsageByBrand> membershipUsageByBrandList){
        return new CountMembershipUsageRes(
                categoryId,
                categoryName,
                sumMembershipUsageByCategory,
                membershipUsageByBrandList
        );
    }
}
