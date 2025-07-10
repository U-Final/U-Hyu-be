package com.ureca.uhyu.domain.user.dto.response;

import java.util.List;

public record GetBookmarkRes (
        List<BookmarkRes> bookmarkIdList
){
}
