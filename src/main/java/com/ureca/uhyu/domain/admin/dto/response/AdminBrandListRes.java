package com.ureca.uhyu.domain.admin.dto.response;


import java.util.List;

public record AdminBrandListRes(
        List<AdminBrandRes> brandList,
        boolean hasNext,
        int totalPages,
        int currentPage
) {
    public static AdminBrandListRes from(List<AdminBrandRes> list, boolean hasNext, int totalPages, int currentPage) {
        return new AdminBrandListRes(list, hasNext, totalPages, currentPage);
    }
}
