package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BarcodeControllerDocs {

    @Operation(summary = "바코드 이미지 업로드", description = "사용자 바코드 이미지를 업로드합니다. 이미지 파일은 multipart/form-data 형식입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "업로드 성공",
            content = @Content(schema = @Schema(example = """
            {
              "statusCode": 0,
              "message": "바코드 이미지가 저장되었습니다.",
              "data": "https://s3.aws.com/.../barcode.png"
            }
            """))
    )
    CommonResponse<String> uploadBarcodeImage(
            @Parameter(hidden = true) @CurrentUser User user,
            @org.springframework.web.bind.annotation.RequestPart MultipartFile image
    );

    @Operation(summary = "바코드 이미지 조회", description = "사용자의 바코드 이미지를 presigned URL로 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(example = """
            {
              "statusCode": 0,
              "message": "정상 처리 되었습니다.",
              "data": "https://s3.aws.com/.../barcode.png"
            }
            """))
    )
    CommonResponse<String> getBarcodeImage(@Parameter(hidden = true) @CurrentUser User user);

    @Operation(summary = "바코드 이미지 수정", description = "사용자의 기존 바코드 이미지를 새로운 이미지로 교체합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(example = """
            {
              "statusCode": 0,
              "message": "바코드 이미지가 저장되었습니다.",
              "data": "https://s3.aws.com/.../barcode.png"
            }
            """))
    )
    CommonResponse<String> updateBarcodeImage(
            @Parameter(hidden = true) @CurrentUser User user,
            @org.springframework.web.bind.annotation.RequestPart MultipartFile image
    );
}
