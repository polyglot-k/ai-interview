package com.example.aiinterview.auth.presentation;

import com.example.aiinterview.auth.application.service.AuthService;
import com.example.aiinterview.auth.application.dto.LoginRequest;
import com.example.aiinterview.auth.application.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public LoginResponse signIn(@RequestBody LoginRequest request){
        return authService.authenticate(request);
    }

}
