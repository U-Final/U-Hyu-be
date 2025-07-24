package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.service.BarcodeService;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/barcode")
public class BarcodeController implements BarcodeControllerDocs {

    private final BarcodeService barcodeService;

    @Override
    @PostMapping
    public CommonResponse<String> uploadBarcodeImage(@CurrentUser User user, MultipartFile image) {
        String url = barcodeService.upload(user, image);
        return CommonResponse.success(ResultCode.BARCODE_UPLOAD_SUCCESS,url);
    }

    @Override
    @GetMapping
    public CommonResponse<String> getBarcodeImage(@CurrentUser User user) {
        return CommonResponse.success(barcodeService.get(user));
    }

    @Override
    @PatchMapping
    public CommonResponse<String> updateBarcodeImage(@CurrentUser User user, MultipartFile image) {
        String url = barcodeService.update(user, image);
        return CommonResponse.success(ResultCode.BARCODE_UPDATE_SUCCESS,url);
    }
}