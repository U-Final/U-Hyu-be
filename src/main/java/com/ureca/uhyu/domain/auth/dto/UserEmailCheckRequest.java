package com.ureca.uhyu.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 중복 확인 요청 DTO")
public record UserEmailCheckRequest (
    @Schema(
            description = "확인할 이메일 주소",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email
) {}
