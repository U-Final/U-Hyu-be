package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.entity.ActionLogs;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "유저 활동내역 응답")
public record UserStatisticsRes(
        Long discountMoney,
        List<Long> bestBrandList
) {
    public static UserStatisticsRes from(ActionLogs actionLogs) {

    }
}
