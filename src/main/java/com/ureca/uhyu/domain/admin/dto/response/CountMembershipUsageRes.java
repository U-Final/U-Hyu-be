package com.ureca.uhyu.domain.admin.dto.response;

import java.util.List;

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
