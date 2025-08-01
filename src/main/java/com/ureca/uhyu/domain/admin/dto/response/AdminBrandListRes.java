package com.ureca.uhyu.domain.admin.dto.response;


import java.util.List;

public record AdminBrandListRes(
        List<ReadBrandRes> brandList,
        boolean hasNext,
        int totalPages,
        int currentPage
) {
    public static AdminBrandListRes from(List<ReadBrandRes> list, boolean hasNext, int totalPages, int currentPage) {
        return new AdminBrandListRes(list, hasNext, totalPages, currentPage);
    }
}
