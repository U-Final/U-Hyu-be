package com.ureca.uhyu.domain.user.dto.response;

import java.util.List;

public record GetBookmarkRes (
        List<BookmarkRes> bookmarkIdList
){
    public static GetBookmarkRes from() {
        return new GetBookmarkRes();
    }
}
