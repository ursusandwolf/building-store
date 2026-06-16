package com.buildstore.user.dto;

import com.buildstore.user.model.UserStatus;
import java.time.Instant;

import java.util.Set;

public record UserResponse(
    Long id,
    String email,
    UserStatus status,
    Set<String> roles,
    Instant createdAt
) {}
