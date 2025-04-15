package com.example.aiinterview.auth.application.service;

import com.example.aiinterview.auth.application.dto.LoginRequest;
import com.example.aiinterview.auth.application.dto.LoginResponse;
import com.example.aiinterview.auth.domain.AccessToken;
import com.example.aiinterview.auth.domain.AuthMember;
import com.example.aiinterview.auth.domain.port.MemberRetrievalPort;
import com.example.aiinterview.auth.domain.RefreshToken;
import com.example.aiinterview.auth.infrastructure.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRetrievalPort memberRetrievalPort;
    private final AuthTokenProvider tokenProvider;
    public LoginResponse authenticate(LoginRequest request){
        //사용자 요청
        AuthMember authMember = memberRetrievalPort.findByEmail(request.email())
                .orElseThrow();

        AccessToken accessToken = tokenProvider.generateAccessToken(authMember);
        RefreshToken refreshToken = tokenProvider.generateRefreshToken(authMember);

        return new LoginResponse(
                accessToken,
                refreshToken
        );
    }
}
