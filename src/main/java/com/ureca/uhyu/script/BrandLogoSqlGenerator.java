package com.ureca.uhyu.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;

//@Component
@Slf4j
@RequiredArgsConstructor
public class BrandLogoSqlGenerator implements CommandLineRunner {

    private static final String BUCKET_NAME = "uhyu-bucket";
    private static final String REGION = "ap-northeast-2";
    private static final String FOLDER_PREFIX = "logo/";
    private static final String[] EXTENSIONS = {"png", "jpg", "jpeg"};
    private static final String OUTPUT_FILE = "logo_update.sql";

    private static final AwsBasicCredentials credentials = AwsBasicCredentials.create(
            System.getenv("AWS_ACCESS_KEY"),
            System.getenv("AWS_SECRET_KEY")
    );

    private final S3Client s3Client = S3Client.builder()
            .region(Region.of(REGION))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

    @Override
    public void run(String... args) {


        List<S3Object> objects;

        try {
            objects= s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(FOLDER_PREFIX)
                    .build()).contents();
        }catch (Exception e){
            throw new RuntimeException("S3 객체 목록 조회 중 오류 발생", e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE, StandardCharsets.UTF_8))) {
            for (S3Object obj : objects) {
                String key = obj.key(); // 예: logo/스타벅스.png

                String fileName = key.replaceFirst(FOLDER_PREFIX, "");
                int dotIdx = fileName.lastIndexOf(".");
                if (dotIdx == -1) continue;

                String brandNameRaw = fileName.substring(0, dotIdx);
                String ext = fileName.substring(dotIdx + 1).toLowerCase();

                boolean valid = false;
                for (String e : EXTENSIONS) {
                    if (e.equals(ext)) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) continue;

                // NFC 정규화 (완성형 보장)
                String brandName = Normalizer.normalize(brandNameRaw, Normalizer.Form.NFC);

                String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
                        .replace("+", "%20")
                        .replace("%2F", "/");

                String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, encodedKey);

                String sql = String.format("UPDATE brands SET logo_image = '%s' WHERE brand_name = '%s';", imageUrl, brandName);
                writer.write(sql);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("SQL 파일 저장 중 오류 발생", e);
        }
    }
}
