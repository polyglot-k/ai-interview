package com.example.aiinterview.module.auth.application.dto;

import com.example.aiinterview.module.auth.domain.AccessToken;
import com.example.aiinterview.module.auth.domain.RefreshToken;

public record LoginResponse(
        AccessToken accessToken,
        RefreshToken refreshToken
) { }
