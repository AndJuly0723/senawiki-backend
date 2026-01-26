package com.senawiki.auth.dto;

import lombok.Getter;

@Getter
public class AuthResponse {

    private final String tokenType = "Bearer";
    private final String accessToken;
    private final String refreshToken;
    private final long expiresInSeconds;
    private final UserResponse user;

    public AuthResponse(String accessToken, String refreshToken, long expiresInSeconds, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresInSeconds = expiresInSeconds;
        this.user = user;
    }
}
