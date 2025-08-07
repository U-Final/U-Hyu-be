package com.ureca.uhyu.domain.admin.dto;

public record MembershipUsageByBrand(
        String brandName,
        Integer sumMembershipUsageByBrand
) {
    public static MembershipUsageByBrand of(String brandName, Integer sumMembershipUsageByBrand) {
        return new MembershipUsageByBrand(
                brandName,
                sumMembershipUsageByBrand
        );
    }
}
