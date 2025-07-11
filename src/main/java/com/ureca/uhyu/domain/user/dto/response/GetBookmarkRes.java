package com.ureca.uhyu.domain.user.dto.response;

import com.ureca.uhyu.domain.user.entity.User;

import java.util.List;

public record GetBookmarkRes (
        List<BookmarkRes> bookmarkIdList
){
    public static GetBookmarkRes from() {
        return new GetBookmarkRes(

        );
    }
}
