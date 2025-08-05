package com.ureca.uhyu.domain.admin.dto.response;

public record StatisticsTotalRes(
        Long totalBookmarkMyMap,
        Long totalFiltering,
        Long totalMembershipUsage
) {
    public static StatisticsTotalRes of(Long totalBookmarkMyMap, Long totalFiltering, Long totalMembershipUsage) {
        return new StatisticsTotalRes(
                totalBookmarkMyMap,
                totalFiltering,
                totalMembershipUsage
        );
    }
}
