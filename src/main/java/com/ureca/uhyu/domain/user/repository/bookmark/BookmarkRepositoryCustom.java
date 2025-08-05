package com.ureca.uhyu.domain.user.repository.bookmark;


import com.ureca.uhyu.domain.admin.dto.response.StatisticsBookmarkMyMapRes;

import java.util.List;

public interface BookmarkRepositoryCustom {
    public List<StatisticsBookmarkMyMapRes> findStatisticsBookmarkByCategoryAndBrand();
}
