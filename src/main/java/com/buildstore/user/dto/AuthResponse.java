package com.buildstore.user.dto;

import com.buildstore.security.service.JwtService;

public record AuthResponse(
    String accessToken,
    String tokenType
) {
    public static AuthResponse bearer(String token) {
        return new AuthResponse(token, JwtService.BEARER_PREFIX.trim());
    }
}
