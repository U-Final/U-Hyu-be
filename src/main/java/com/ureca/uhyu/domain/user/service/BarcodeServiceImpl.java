package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.user.entity.Barcode;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.repository.barcode.BarcodeRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import com.ureca.uhyu.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    private final BarcodeRepository barcodeRepository;
    private final S3Uploader s3Uploader;

    private static final String FOLDER = "uhyu-barcode";

    @Override
    @Transactional
    public String upload(User user, MultipartFile image) {
        String key = s3Uploader.upload(image, FOLDER);

        Barcode barcode = Barcode.builder()
                .user(user)
                .imageURL(key)
                .build();

        barcodeRepository.save(barcode);

        return s3Uploader.generatePresignedUrl(key);
    }

    @Override
    @Transactional
    public String update(User user, MultipartFile image) {
        Barcode barcode = barcodeRepository.findByUser(user)
                .orElseThrow(() -> new GlobalException(ResultCode.BARCODE_NOT_FOUND));

        s3Uploader.delete(barcode.getImageURL());

        String newUrl = s3Uploader.upload(image, FOLDER);

        barcode.updateImageUrl(newUrl);
        return s3Uploader.generatePresignedUrl(newUrl);
    }

    @Override
    public String get(User user) {
        Barcode barcode = barcodeRepository.findByUser(user)
                .orElseThrow(() -> new GlobalException(ResultCode.BARCODE_NOT_FOUND));

        return s3Uploader.generatePresignedUrl(barcode.getImageURL());
    }
}
