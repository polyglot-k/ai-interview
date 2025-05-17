package com.example.aiinterview.module.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "새로운 유저 생성 요청")
public record CreateMemberRequest(
        @Schema(description = "이메일", example = "example@example.com") String email,
        @Schema(description = "이름", example = "홍길동") String name,
        @Schema(description = "비밀번호") String password) {
}
