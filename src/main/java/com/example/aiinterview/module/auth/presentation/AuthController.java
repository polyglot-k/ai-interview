package com.example.aiinterview.module.auth.presentation;

import com.example.aiinterview.module.auth.application.dto.LoginRequest;
import com.example.aiinterview.module.auth.application.dto.LoginResponse;
import com.example.aiinterview.module.auth.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public Mono<LoginResponse> signIn(@RequestBody LoginRequest request){
        return authService.authenticate(request);
    }

    @PostMapping("/refresh")
    public Mono<Void> refresh(){
        return Mono.empty();
    }
}
