package com.ureca.uhyu.global.util;

import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");

    public String uploadBarcode(MultipartFile file, String folderName) {
        String originalName = file.getOriginalFilename();
        validateFileExtension(originalName);

        String key = folderName + "/" + UUID.randomUUID() + "_" + originalName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return key;
        } catch (IOException e) {
            throw new GlobalException(ResultCode.S3_UPLOAD_FAIL, e);
        }
    }

    public void delete(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    public String generatePresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private void validateFileExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new GlobalException(ResultCode.INVALID_FILE_EXTENSION);
        }
    }
}
