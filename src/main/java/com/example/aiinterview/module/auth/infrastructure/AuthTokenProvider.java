package com.example.aiinterview.module.auth.infrastructure;

import com.example.aiinterview.common.utils.JWTUtils;
import com.example.aiinterview.module.auth.domain.AccessToken;
import com.example.aiinterview.common.security.AuthorizationPayload;
import com.example.aiinterview.module.auth.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenProvider {
    private final JWTUtils jwtUtils;
    private static final long ACCESS_TOKEN_EXPIRES_IN = 1_000_000;
    private static final long REFRESH_TOKEN_EXPIRES_IN = 1_000_000;
    public AccessToken generateAccessToken(AuthorizationPayload payload){
        String token = jwtUtils.generateToken(payload, ACCESS_TOKEN_EXPIRES_IN);
        return AccessToken.of(token);
    }

    public RefreshToken generateRefreshToken(AuthorizationPayload payload){
        String token = jwtUtils.generateToken(payload, REFRESH_TOKEN_EXPIRES_IN);
        return RefreshToken.of(token);
    }
}
