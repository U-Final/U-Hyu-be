package com.ureca.uhyu.domain.admin.dto.response;

public record CountTotalRes(
        Long totalBookmark,
        Long totalFiltering,
        Long totalSearch,
        Long totalMembershipUsage
) {
    public static CountTotalRes of(Long totalBookmark, Long totalFiltering, Long totalSearch, Long totalMembershipUsage) {
        return new CountTotalRes(
                totalBookmark,
                totalFiltering,
                totalSearch,
                totalMembershipUsage
        );
    }
}
