package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.entity.ActionLogs;
import com.ureca.uhyu.domain.user.entity.History;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "유저 활동내역 응답")
public record UserStatisticsRes(
        Integer discountMoney,
        List<BestBrandListRes> bestBrandList,
        List<RecentStoreListRes> recentStoreList
) {
    public static UserStatisticsRes from(
            Integer discountMoney,
            List<BestBrandListRes> bestBrandList,
            List<RecentStoreListRes> recentStoreList
    ) {
        return new UserStatisticsRes(
                discountMoney,
                bestBrandList,
                recentStoreList
        );
    }
}
