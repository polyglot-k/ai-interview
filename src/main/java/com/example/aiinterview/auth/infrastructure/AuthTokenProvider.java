package com.example.aiinterview.auth.infrastructure;

import com.example.aiinterview.auth.domain.AccessToken;
import com.example.aiinterview.auth.domain.AuthMember;
import com.example.aiinterview.auth.domain.RefreshToken;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenProvider {
    public AccessToken generateAccessToken(AuthMember member){
        return AccessToken.of("");
    }

    public RefreshToken generateRefreshToken(AuthMember authMember){
        return RefreshToken.of("");
    }
}
