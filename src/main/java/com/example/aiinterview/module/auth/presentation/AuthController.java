package com.example.aiinterview.module.auth.presentation;

import com.example.aiinterview.global.common.response.ResponseFactory;
import com.example.aiinterview.global.common.response.dto.ApiResponse;
import com.example.aiinterview.module.auth.application.dto.LoginRequest;
import com.example.aiinterview.module.auth.application.dto.LoginResponse;
import com.example.aiinterview.module.auth.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name="AUTH API", description = "인증 관련 기능을 제공하는 API 입니다.")
@Slf4j
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "로그인 요청", description = "이메일과 비밀번호를 통한 로그인을 진행한다.")
    public Mono<ApiResponse<LoginResponse>> signIn(@RequestBody LoginRequest request){
        log.info(request.toString());
        return authService.authenticate(request)
                .flatMap(ResponseFactory::successMono);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "미구현")
    public Mono<Void> refresh(){
        return Mono.empty();
    }
}
