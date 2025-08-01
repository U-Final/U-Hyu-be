package com.ureca.uhyu.domain.user.repository.bookmark;


import com.ureca.uhyu.domain.admin.dto.response.StatisticsBookmarkRes;

import java.util.List;

public interface BookmarkRepositoryCustom {
    public List<StatisticsBookmarkRes> findStatisticsBookmarkByCategoryAndBrand();
}
