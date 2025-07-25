package com.ureca.uhyu.domain.admin.dto.response;

public record CountTotalRes(
        Integer totalBookmark,
        Integer totalFiltering,
        Integer totalSearch,
        Integer totalMembershipUsage
) {
    public static CountTotalRes of(Integer totalBookmark, Integer totalFiltering, Integer totalSearch, Integer totalMembershipUsage) {
        return new CountTotalRes(
                totalBookmark,
                totalFiltering,
                totalSearch,
                totalMembershipUsage
        );
    }
}
