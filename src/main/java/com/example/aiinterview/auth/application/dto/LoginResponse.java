package com.example.aiinterview.auth.application.dto;

import com.example.aiinterview.auth.domain.AccessToken;
import com.example.aiinterview.auth.domain.RefreshToken;

public record LoginResponse(
        AccessToken accessToken,
        RefreshToken refreshToken
) { }
