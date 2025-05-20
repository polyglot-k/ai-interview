package com.example.aiinterview.module.auth.application.service;

import com.example.aiinterview.global.common.utils.CryptUtils;
import com.example.aiinterview.global.security.AuthorizationPayload;
import com.example.aiinterview.module.auth.application.dto.LoginRequest;
import com.example.aiinterview.module.auth.application.dto.LoginResponse;
import com.example.aiinterview.module.auth.domain.AccessToken;
import com.example.aiinterview.module.auth.domain.RefreshToken;
import com.example.aiinterview.module.auth.domain.port.MemberRetrievalPort;
import com.example.aiinterview.module.auth.exception.InvalidPasswordException;
import com.example.aiinterview.module.auth.infrastructure.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRetrievalPort memberRetrievalPort;
    private final AuthTokenProvider tokenProvider;

    public Mono<LoginResponse> authenticate(LoginRequest request) {
        return memberRetrievalPort.findByEmail(request.email())
                .flatMap(member ->
                        Mono.fromCallable(() -> CryptUtils.matches(request.password(), member.getPassword()))
                                .subscribeOn(Schedulers.boundedElastic()) // 🔥 BCrypt는 별도 스레드에서 실행
                                .flatMap(passwordMatches -> {
                                    if (!passwordMatches) {
                                        return Mono.error(new InvalidPasswordException());
                                    }

                                    AuthorizationPayload payload = new AuthorizationPayload(
                                            member.getId(),
                                            member.getName(),
                                            member.getEmail()
                                    );

                                    AccessToken accessToken = tokenProvider.generateAccessToken(payload);
                                    RefreshToken refreshToken = tokenProvider.generateRefreshToken(payload);

                                    return Mono.just(new LoginResponse(accessToken, refreshToken));
                                })
                );
    }
}
