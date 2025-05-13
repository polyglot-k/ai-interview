package com.example.aiinterview.module.auth.application.service;

import com.example.aiinterview.global.common.utils.CryptUtils;
import com.example.aiinterview.global.security.AuthorizationPayload;
import com.example.aiinterview.module.auth.application.dto.LoginRequest;
import com.example.aiinterview.module.auth.application.dto.LoginResponse;
import com.example.aiinterview.module.auth.domain.AccessToken;
import com.example.aiinterview.module.auth.domain.RefreshToken;
import com.example.aiinterview.module.auth.domain.port.MemberRetrievalPort;
import com.example.aiinterview.module.auth.infrastructure.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRetrievalPort memberRetrievalPort;
    private final AuthTokenProvider tokenProvider;

    public Mono<LoginResponse> authenticate(LoginRequest request) {
        return memberRetrievalPort.findByEmail(request.email())
                .flatMap(member -> {
                    if (!CryptUtils.matches(request.password(),member.getPassword())) {
                        return Mono.error(new RuntimeException("비밀번호가 일치하지 않습니다."));
                    }

                    AuthorizationPayload payload = new AuthorizationPayload(
                            member.getId(),
                            member.getName(),
                            member.getEmail()
                    );
                    AccessToken accessToken = tokenProvider.generateAccessToken(payload);
                    RefreshToken refreshToken = tokenProvider.generateRefreshToken(payload);

                    return Mono.just(new LoginResponse(accessToken, refreshToken));
                });
    }
}
