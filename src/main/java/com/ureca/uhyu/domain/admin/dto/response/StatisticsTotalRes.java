package com.ureca.uhyu.domain.admin.dto.response;

public record StatisticsTotalRes(
        Long totalBookmark,
        Long totalFiltering,
        Long totalMembershipUsage
) {
    public static StatisticsTotalRes of(Long totalBookmark, Long totalFiltering, Long totalMembershipUsage) {
        return new StatisticsTotalRes(
                totalBookmark,
                totalFiltering,
                totalMembershipUsage
        );
    }
}
